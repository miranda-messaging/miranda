package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/21/2017.
 */
public class ShutdownResponseMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public ShutdownResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.ShutdownResponse, senderQueue, sender);

        this.name = name;
    }
}
