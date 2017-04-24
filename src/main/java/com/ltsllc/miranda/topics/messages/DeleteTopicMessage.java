package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class DeleteTopicMessage extends Message {
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public DeleteTopicMessage (BlockingQueue<Message> senderQueue, Object sender, String topicName) {
        super(Subjects.DeleteTopic, senderQueue, sender);

        this.topicName = topicName;
    }
}
