package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/5/2017.
 */
public class NodeAddedMessage extends Message {
    private Node node;


    public NodeAddedMessage(BlockingQueue<Message> sender, Node node) {
        super(Subjects.NodeAdded, null, null);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
