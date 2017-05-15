package com.ltsllc.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
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

    public UpdateTopicOperation (BlockingQueue<Message> requester, Session session, Topic topic) {
        super("update topic operations", requester, session);

        UpdateTopicOperationReadyState updateTopicOperationReadyState = new UpdateTopicOperationReadyState(this);
        setCurrentState(updateTopicOperationReadyState);

        this.topic = topic;
    }

    public void start () {
        super.start();

        Miranda.getInstance().getUserManager().sendGetUser(getQueue(), this, getTopic().getOwner());
    }
}
