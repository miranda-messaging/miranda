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

package com.ltsllc.miranda.writer;

import com.google.gson.Gson;
import com.ltsllc.clcl.EncryptedMessage;
import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.clcl.PublicKey;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Writer extends Consumer {
    private static Writer instance;
    private static Gson gson = new Gson();

    private PublicKey publicKey;
    private boolean debugMode;

    public static Writer getInstance() {
        return instance;
    }

    public static void setInstance(Writer instance) {
        Writer.instance = instance;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Writer(boolean isDebugMode, PublicKey publicKey) throws MirandaException {
        super("writer");
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);

        WriterReadyState writerReadyState = new WriterReadyState(this);
        setCurrentState(writerReadyState);

        setDebugMode(isDebugMode);
        this.publicKey = publicKey;
        setInstance(this);
    }

    public void write(String filename, byte[] clearText) throws IOException, EncryptionException {
        File file = new File(filename);

        if (file.exists())
            backup(file);

        if (isDebugMode())
            writeUnencrypted(filename, clearText);
        else
            writeEncrypted(filename, clearText);
    }


    public void writeUnencrypted (String filename, byte[] clearText) throws IOException {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(clearText);
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public void writeEncrypted (String filename, byte[] clearText) throws IOException, EncryptionException {
        FileWriter fileWriter = null;

        try {
            EncryptedMessage encryptedMessage = encrypt(clearText);
            fileWriter = new FileWriter(filename);
            String json = gson.toJson(encryptedMessage);
            fileWriter.write(json);
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    private static final int BUFFER_SIZE = 8192;

    public void copyFile(String srcFileName, String destFileName) throws IOException {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            fileInputStream = new FileInputStream(srcFileName);
            fileOutputStream = new FileOutputStream(destFileName);

            int bytesRead = fileInputStream.read(buffer);
            while (bytesRead != -1) {
                fileOutputStream.write(buffer);
                bytesRead = fileInputStream.read(buffer);
            }
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public void backup(File file) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        File backup = new File(file.getCanonicalPath() + ".backup");
        if (backup.exists()) {
            if (!backup.delete()) {
                throw new IOException("Could not remove backup file: " + backup);
            }
        }

        String filename = file.getCanonicalPath();
        String backupFilename = filename + ".backup";

        copyFile(filename, backupFilename);
    }

    public void sendWrite(BlockingQueue<Message> senderQueue, Object sender, String filename, byte[] data) {
        WriteMessage writeMessage = new WriteMessage(filename, data, senderQueue, sender);
        sendToMe(writeMessage);
    }

    public EncryptedMessage encrypt(byte[] plaintext) throws EncryptionException {
        return getPublicKey().encryptToMessage(plaintext);
    }
}
