package com.ltsllc.miranda.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/23/2017.
 */
public class DeleteTopicOperation extends Operation {
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public DeleteTopicOperation (String topicName, BlockingQueue<Message> requester) {
        super("delete topic operation", requester);

        this.topicName = topicName;
    }

    public void start () {
        Miranda.getInstance().getTopicManager().sendDeleteTopicMessage(getQueue(), this, getTopicName());
    }
}
