package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/7/2017.
 */
public class ReadEventMessage extends Message {
    private String guid;

    public ReadEventMessage(BlockingQueue<Message> senderQueue, Object sender, String guid) {
        super(Subjects.Read, senderQueue, sender);

        this.guid = guid;
    }
}
