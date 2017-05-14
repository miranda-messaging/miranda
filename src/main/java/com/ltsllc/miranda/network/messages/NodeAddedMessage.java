package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/5/2017.
 */
public class NodeAddedMessage extends Message {
    private Node node;


    public NodeAddedMessage(BlockingQueue<Message> senderQueue, Object sender, Node node) {
        super(Subjects.NodeAdded, senderQueue, sender);

        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
