package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/3/2017.
 */
public class LoadMessage extends Message {
    private String filename;

    public LoadMessage (BlockingQueue<Message> queue, String filename, Object senderObject) {
        super(Subjects.Load, queue, senderObject);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
