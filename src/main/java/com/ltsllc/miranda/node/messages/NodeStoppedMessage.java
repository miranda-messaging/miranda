package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/18/2017.
 */
public class NodeStoppedMessage extends Message {
    private Node node;

    public Node getNode() {
        return node;
    }

    public NodeStoppedMessage (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        super (Subjects.NodeStopped,senderQueue, sender);

        this.node = node;
    }
}
