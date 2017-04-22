package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class OwnerQueryMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public OwnerQueryMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.OwnerQuery, senderQueue, sender);

        this.name = name;
    }
}
