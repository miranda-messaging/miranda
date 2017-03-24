package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;

import com.ltsllc.miranda.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Writer extends Consumer {
    private static Writer ourInstance;

    public static synchronized void setInstance (Writer writer) {
        if (null == ourInstance)
            ourInstance = writer;
    }

    public static Writer getInstance () {
        return ourInstance;
    }

    public Writer () {
        super("writer");
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
        setCurrentState(new WriterReadyState(this));

        setInstance(this);
    }

    public void write (String filename, byte[] data) throws IOException {
        File file = new File(filename);

        if (file.exists())
            backup(file);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(filename);
            fos.write(data);
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
}
