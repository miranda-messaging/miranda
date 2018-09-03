package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Ask for the number of nodes in the Cluster
 */
public class GetNodeCountMessage extends Message {
    public GetNodeCountMessage(BlockingQueue<Message> queue, Object senderObject) {
        super (Subjects.GetNodeCount, queue, senderObject);
    }
}
