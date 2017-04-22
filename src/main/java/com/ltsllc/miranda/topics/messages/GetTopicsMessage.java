package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class GetTopicsMessage extends Message {
    public GetTopicsMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetTopics, senderQueue, sender);
    }
}
