package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NodeElement;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */

/**
 * The {@link ClusterFile} discovered a new node.
 */
public class NewNodeElementMessage extends Message {
    private NodeElement node;

    public NewNodeElementMessage (BlockingQueue<Message> senderQueue, Object sender, NodeElement newNode) {
        super(Subjects.NewNodeElement, senderQueue, sender);

        this.node = newNode;
    }

    public NodeElement getNode() {
        return node;
    }
}
