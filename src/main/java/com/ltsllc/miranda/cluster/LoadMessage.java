package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/3/2017.
 */
public class LoadMessage extends Message {
    private String filename;

    public LoadMessage (BlockingQueue<Message> queue, String filename) {
        super(Subjects.Load, queue);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}