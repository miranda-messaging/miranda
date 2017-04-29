package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.SessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class CreateTopicMessage extends SessionMessage {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public CreateTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, Topic topic) {
        super(Subjects.CreateTopic, senderQueue, sender, session);

        this.topic = topic;
    }
}
