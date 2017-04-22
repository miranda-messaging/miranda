package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class NewTopicResponseMessage extends Message {
    private boolean topicCreated;

    public boolean getTopicCreated () {
        return this.topicCreated;
    }

    public NewTopicResponseMessage (BlockingQueue<Message> senderQueue, Object sender, boolean topicCreated) {
        super(Subjects.NewTopicResponse, senderQueue, sender);

        this.topicCreated = topicCreated;
    }
}
