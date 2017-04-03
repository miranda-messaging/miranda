package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/2/2017.
 */
public class GetUserMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public GetUserMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.GetUser, senderQueue, sender);

        this.name = name;
    }
}
