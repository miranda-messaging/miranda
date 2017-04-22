package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class GetTopicResponseMessage extends Message {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public GetTopicResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Topic topic) {
        super(Subjects.GetTopicResponse, senderQueue, sender);
    }
}
