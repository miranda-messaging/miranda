package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class DeleteTopicMessage extends Message {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public DeleteTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Topic topic) {
        super(Subjects.DeleteTopic, senderQueue, sender);

        this.topic = topic;
    }
}
