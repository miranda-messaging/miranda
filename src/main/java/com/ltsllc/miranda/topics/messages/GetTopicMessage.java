package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class GetTopicMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public GetTopicMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.GetTopic, senderQueue, sender);

        this.name = name;
    }
}
