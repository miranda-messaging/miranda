package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/13/2017.
 */
public class UserDeletedMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public UserDeletedMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.UserDeleted, senderQueue, sender);

        this.name = name;
    }
}
