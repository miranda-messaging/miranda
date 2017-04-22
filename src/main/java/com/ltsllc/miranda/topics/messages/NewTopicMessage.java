package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class NewTopicMessage extends Message {
    private Topic topic;

    public NewTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Topic topic) {
        super(Subjects.NewTopic, senderQueue, sender);

        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }
}
