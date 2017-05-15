package com.ltsllc.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateTopicOperation extends Operation {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public CreateTopicOperation (BlockingQueue<Message> requester, Session session, Topic topic) {
        super ("create topic operations", requester, session);

        CreateTopicOperationReadyState readyState = new CreateTopicOperationReadyState(this);
        setCurrentState(readyState);

        this.topic = topic;
    }

    public void start () {
        Miranda.getInstance().getUserManager().sendGetUser(getQueue(), this, getTopic().getOwner());
    }
}
