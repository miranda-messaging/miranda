package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/3/2017.
 */
public class ReadMessage extends Message {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public ReadMessage (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        super(Subjects.Read, senderQueue, sender);

        this.filename = filename;
    }
}
