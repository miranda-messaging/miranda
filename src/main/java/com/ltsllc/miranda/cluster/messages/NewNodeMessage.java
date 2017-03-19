package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

public class NewNodeMessage extends Message {
    private Node node;

    public Node getNode() {
        return node;
    }

    public NewNodeMessage (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        super(Subjects.NewNode, senderQueue, sender);

        this.node = node;
    }
}
