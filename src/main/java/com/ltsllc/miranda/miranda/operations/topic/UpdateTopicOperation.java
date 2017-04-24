package com.ltsllc.miranda.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/23/2017.
 */
public class UpdateTopicOperation extends Operation {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public UpdateTopicOperation (Topic topic, BlockingQueue<Message> requester) {
        super("update topic operation", requester);

        this.topic = topic;
    }

    public void start () {
        Miranda.getInstance().getUserManager().sendGetUser(getQueue(), this, getTopic().getOwner());
    }
}
