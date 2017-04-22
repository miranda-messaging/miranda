package com.ltsllc.miranda.session.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/15/2017.
 */
public class GetSessionMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public GetSessionMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.GetSession, senderQueue, sender);

        this.name = name;
    }
}
