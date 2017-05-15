package com.ltsllc.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/23/2017.
 */
public class DeleteTopicOperation extends Operation {
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public DeleteTopicOperation (BlockingQueue<Message> requester, Session session, String topicName) {
        super("delete topic operation", requester, session);

        this.topicName = topicName;
    }

    public void start () {
        Miranda.getInstance().getTopicManager().sendDeleteTopicMessage(getQueue(), this, getTopicName());
    }
}
