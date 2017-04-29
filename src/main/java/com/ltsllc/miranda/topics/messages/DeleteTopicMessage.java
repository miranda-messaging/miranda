package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.SessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class DeleteTopicMessage extends SessionMessage {
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public DeleteTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, String topicName) {
        super(Subjects.DeleteTopic, senderQueue, sender, session);

        this.topicName = topicName;
    }
}
