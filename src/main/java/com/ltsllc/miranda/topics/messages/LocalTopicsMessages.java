package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.message.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * The topics that are local to a system
 */
public class LocalTopicsMessages extends Message {
    private List<String> topics;

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public LocalTopicsMessages (BlockingQueue<Message> queue, Object senderObject, List<String> topics) {
        super(Subjects.LocalTopics, queue, senderObject);
        setTopics(topics);
    }
}
