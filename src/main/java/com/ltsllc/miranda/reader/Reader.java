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
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.messages.ReadMessage;
import com.ltsllc.miranda.reader.messages.ScanMessage;
import org.apache.log4j.Logger;

import java.io.*;
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

        public void setAdditionalInfo(Throwable t) {
            this.exception = t;
        }
    }

    public static String NAME = "reader";

    private static Logger logger = Logger.getLogger(Reader.class);
    private static Gson gson = new Gson();

    private PrivateKey privateKey;
    private boolean debugMode;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public Reader(boolean isDebugMode, PrivateKey privateKey) throws MirandaException {
        super(NAME);

        this.privateKey = privateKey;
        setDebugMode(isDebugMode);

        ReaderReadyState readerReadyState = new ReaderReadyState(this);
        setCurrentState(readerReadyState);
    }

    public ReadResult read(String filename) {
        if (isDebugMode())
            return readUnencrypted(filename);
        else
            return readEncrypted(filename);
    }

    public ReadResult readUnencrypted (String filename) {
        ReadResult readResult = new ReadResult();
        readResult.filename = filename;

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            readResult.data = Utils.readCompletely(fileInputStream);
            readResult.result = Results.Success;
        } catch (FileNotFoundException e) {
            readResult.exception = e;
            readResult.result = Results.FileDoesNotExist;
        } catch (Throwable e) {
            readResult.exception = e;
            readResult.result = Results.ExceptionReadingFile;
        }

        return readResult;
    }


    public ReadResult readEncrypted (String filename) {
        ReadResult result = new ReadResult();
        FileReader fileReader = null;
        result.result = Results.Unknown;
        result.filename = filename;

        File file = new File(filename);
        if (!file.exists()) {
            result.result = Results.FileDoesNotExist;
        } else {
            EncryptedMessage encryptedMessage = null;
            try {
                fileReader = new FileReader(filename);
                encryptedMessage = readEncryptedMessage(fileReader);
            } catch (Exception e) {
                result.result = Results.ExceptionReadingFile;
                result.exception = e;
            }

            if (null != encryptedMessage) {
                try {
                    result.data = decryptMessage(encryptedMessage);
                    result.result = Results.Success;
                } catch (EncryptionException e) {
                    result.result = Results.ExceptionDecryptingFile;
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

    public void sendScan (String filename, BlockingQueue<Message> senderQueue, Object senderObject) {
        ScanMessage scanMessage = new ScanMessage(filename, senderQueue, senderObject);
        sendToMe(scanMessage);
    }
}
