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

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.PrivateKey;
import com.ltsllc.miranda.Results;
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
        public String additionalInfo;

        public void setAdditionalInfo (Throwable t) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            t.printStackTrace(printWriter);
        }
    }

    public static String NAME = "reader";

    private static Logger logger = Logger.getLogger(Reader.class);

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

    public ReadResult read (String filename) {
        ReadResult result = new ReadResult();
        FileInputStream fileInputStream = null;
        result.result = Results.Unknown;
        result.filename = filename;

        File file = new File (filename);
        long size = file.length();
        if (size > Integer.MAX_VALUE) {
            result.result = Results.FileTooLarge;
            result.additionalInfo = "File " + filename + " is too large.  Max size:" + Integer.MAX_VALUE + " file size " + size;
        } else if (!file.exists()) {
            result.result = Results.FileNotFound;
        } else {
            try {
                fileInputStream = new FileInputStream(file);
                int intSize = (int) size;
                byte[] ciphertext = new byte[intSize];
                int bytesRead = fileInputStream.read(ciphertext);

                if (bytesRead < intSize) {
                    result.result = Results.ShortRead;
                } else {
                    result.data = decrypt(ciphertext);
                    result.result = Results.Success;
                }
            } catch (IOException | GeneralSecurityException e) {
                result.result = Results.Exception;
                result.setAdditionalInfo(e);
            }
        }

        return result;
    }

    public byte[] decrypt (byte[] ciphertext) throws GeneralSecurityException{
        return getPrivateKey().decrypt(ciphertext);
    }

    public void sendReadMessage (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        ReadMessage readMessage = new ReadMessage (senderQueue, sender, filename);
        sendToMe(readMessage);
    }
}
