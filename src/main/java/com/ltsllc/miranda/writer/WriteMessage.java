package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class WriteMessage extends Message {
    private String filename;

    public String getFilename () {
        return filename;
    }

    public void setFilename (String s) {
        filename = s;
    }

    private byte[] buffer;

    public byte[] getBuffer () {
        return buffer;
    }

    public void setBuffer (byte[] b) {
        buffer = b;
    }

    public WriteMessage (String filename, byte[] buffer, BlockingQueue<Message> sender, Object senderObject) {
        super(Subjects.Write, sender, senderObject);
        setBuffer(buffer);
        setFilename(filename);
    }
}
