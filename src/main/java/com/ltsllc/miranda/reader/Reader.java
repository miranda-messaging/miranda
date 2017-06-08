/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.reader;

import com.google.gson.Gson;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 5/3/2017.
 */
public class Reader extends Consumer {
    public static class ReadResult {
        public Results result;
        public String filename;
        public byte[] data;
        public Throwable exception;

        public void setAdditionalInfo (Throwable t) {
            this.exception = t;
        }
    }

    public static String NAME = "reader";

    private static Logger logger = Logger.getLogger(Reader.class);
    private static Gson gson = new Gson();

    private PrivateKey privateKey;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public Reader (PrivateKey privateKey) {
        super (NAME);

        this.privateKey = privateKey;

        ReaderReadyState readerReadyState = new ReaderReadyState(this);
        setCurrentState(readerReadyState);
    }

    public ReadResult read (String filename) throws IOException, GeneralSecurityException {
        ReadResult result = new ReadResult();
        FileReader fileReader = null;
        FileInputStream fileInputStream = null;
        result.result = Results.Unknown;
        result.filename = filename;

        File file = new File (filename);
        if (!file.exists()) {
            result.result = Results.FileNotFound;
        } else {
            try {
                fileReader = new FileReader(file);
                EncryptedMessage encryptedMessage = gson.fromJson(fileReader, EncryptedMessage.class);
                byte[] plainText = getPrivateKey().decrypt(encryptedMessage);
                result.result = Results.Success;
                result.data = plainText;
            } catch (Exception e) {
                result.result = Results.Exception;
                result.exception = e;
            } finally {
                Utils.closeIgnoreExceptions(fileReader);
            }
        }

        return result;
    }

    public byte[] decrypt (EncryptedMessage encryptedMessage) throws GeneralSecurityException, IOException {
        return getPrivateKey().decrypt(encryptedMessage);
    }

    public void sendReadMessage (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        ReadMessage readMessage = new ReadMessage (senderQueue, sender, filename);
        sendToMe(readMessage);
    }

    public void sendReadMessage (BlockingQueue<Message> senderQueue, Object sender, File file) {
        try {
            String canonicalPath = file.getCanonicalPath();
            ReadMessage readMessage = new ReadMessage(senderQueue, sender, canonicalPath);
            sendToMe(readMessage);
        } catch (IOException e) {
            Panic panic = new Panic("Excepion trying to get canical path of " + file.getName(), e,
                    Panic.Reasons.ExceptionLoadingFile);

            Miranda.panicMiranda(panic);
        }
    }
}
