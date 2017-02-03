package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class WriteSucceededMessage extends Message {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public WriteSucceededMessage (BlockingQueue<Message> sender, String filename) {
        super(Subjects.WriteSucceeded, sender);
        this.filename = filename;
    }
}
