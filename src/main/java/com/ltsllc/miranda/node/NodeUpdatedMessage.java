package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */

/**
 * Indicates that the sender has changed.
 */
public class NodeUpdatedMessage extends Message {
    private Node node;

    public Node getNode() {
        return node;
    }

    public NodeUpdatedMessage (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        super(Subjects.NodeUpdated, senderQueue, sender);

        this.node = node;
    }
}
