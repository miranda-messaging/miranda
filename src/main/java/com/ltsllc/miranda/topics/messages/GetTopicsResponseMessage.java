package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.topics.Topic;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class GetTopicsResponseMessage extends Message {
    private List<Topic> topics;

    public List<Topic> getTopics() {
        return topics;
    }

    public GetTopicsResponseMessage (BlockingQueue<Message> senderQueue, Object sender, List<Topic> topics) {
        super(Subjects.GetTopicsResponse, senderQueue, sender);

        this.topics = topics;
    }
}
