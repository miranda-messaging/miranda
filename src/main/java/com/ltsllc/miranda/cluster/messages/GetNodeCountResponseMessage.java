package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * the number of nodes in the cluster
 */
public class GetNodeCountResponseMessage extends Message {
    public int nodeCount;

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public GetNodeCountResponseMessage(BlockingQueue<Message> queue, Object senderObject, int nodeCount) {
        super (Subjects.GetNodeCountResponse, queue, senderObject);
        setNodeCount(nodeCount);
    }
}
