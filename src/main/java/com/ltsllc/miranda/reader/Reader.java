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
import com.ltsllc.clcl.EncryptedMessage;
import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.clcl.PrivateKey;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 5/3/2017.
 */
public class Reader extends Consumer {
    public static class ReadResult {
        public ReadResponseMessage.Results result;
        public String filename;
        public byte[] data;
        public Throwable exception;

        public void setAdditionalInfo(Throwable t) {
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

    public Reader(PrivateKey privateKey) throws MirandaException {
        super(NAME);

        this.privateKey = privateKey;

        ReaderReadyState readerReadyState = new ReaderReadyState(this);
        setCurrentState(readerReadyState);
    }

    public ReadResult read(String filename) {
        ReadResult result = new ReadResult();
        FileReader fileReader = null;
        result.result = ReadResponseMessage.Results.Unknown;
        result.filename = filename;

        File file = new File(filename);
        if (!file.exists()) {
            result.result = ReadResponseMessage.Results.FileDoesNotExist;
        } else {
            EncryptedMessage encryptedMessage = null;
            try {
                fileReader = new FileReader(filename);
                encryptedMessage = readEncryptedMessage(fileReader);
            } catch (Exception e) {
                result.result = ReadResponseMessage.Results.ExceptionReadingFile;
                result.exception = e;
            }

            if (null != encryptedMessage) {
                try {
                    result.data = decryptMessage(encryptedMessage);
                    result.result = ReadResponseMessage.Results.Success;
                } catch (EncryptionException e) {
                    result.result = ReadResponseMessage.Results.ExceptionDecryptingFile;
                    result.exception = e;
                }
            }
        }

        return result;
    }

    public EncryptedMessage readEncryptedMessage(java.io.Reader reader) {
        return gson.fromJson(reader, EncryptedMessage.class);
    }

    public byte[] decryptMessage(EncryptedMessage encryptedMessage) throws EncryptionException {
        return getPrivateKey().decrypt(encryptedMessage);
    }

    public void sendReadMessage(BlockingQueue<Message> senderQueue, Object sender, String filename) {
        ReadMessage readMessage = new ReadMessage(senderQueue, sender, filename);
        sendToMe(readMessage);
    }

    public void sendReadMessage(BlockingQueue<Message> senderQueue, Object sender, File file) {
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
