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

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Writer extends Consumer {
    private static Writer ourInstance;

    private PublicKey publicKey;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static synchronized void setInstance (Writer writer) {
        if (null == ourInstance)
            ourInstance = writer;
    }

    public static Writer getInstance () {
        return ourInstance;
    }

    public Writer (PublicKey publicKey) {
        super("writer");
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
        setCurrentState(new WriterReadyState(this));

        setInstance(this);

        this.publicKey = publicKey;
    }

    public void write (String filename, byte[] data) throws IOException {
        File file = new File(filename);

        if (file.exists())
            backup(file);

        FileOutputStream fos = null;

        byte[] ciphertext = null;

        try {
            ciphertext = encrypt(data);
        } catch (GeneralSecurityException e) {
            Panic panic = new Panic("Attempt to encrypt " + filename + " failed", e, Panic.Reasons.EncryptException);
            Miranda.getInstance().panic(panic);
        }

        try {
            fos = new FileOutputStream(filename);
            fos.write(ciphertext);
        } finally {
            Utils.closeIgnoreExceptions(fos);
        }
    }

    private static final int BUFFER_SIZE = 8192;

    public void backup (File file) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];

        File backup = new File(file.getCanonicalPath() + ".backup");
        if (backup.exists()) {
            if (!backup.delete()) {
                throw new IOException ("Could not remove backup file: " + backup);
            }
        }

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(backup);

            int bytesRead = fileInputStream.read(buffer);
            do {
                fileOutputStream.write(buffer,0, bytesRead);
                bytesRead = fileInputStream.read(buffer);
            } while (bytesRead == buffer.length);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public void sendWrite (BlockingQueue<Message> senderQueue, Object sender, String filename, byte[] data) {
        WriteMessage writeMessage = new WriteMessage(filename, data, senderQueue, sender);
        sendToMe(writeMessage);
    }

    public byte[] encrypt (byte[] plaintext) throws GeneralSecurityException {
        return getPublicKey().encrypt(plaintext);
    }
}
