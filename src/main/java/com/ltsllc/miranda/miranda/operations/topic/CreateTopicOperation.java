package com.ltsllc.miranda.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
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

    public CreateTopicOperation (Topic topic, BlockingQueue<Message> requester) {
        super ("create topic operation", requester);

        CreateTopicOperationReadyState readyState = new CreateTopicOperationReadyState(this);
        setCurrentState(readyState);

        this.topic = topic;
    }

    public void start () {
        Miranda.getInstance().getTopicManager().sendCreateTopicMessage(getQueue(), this, getTopic());
    }
}
