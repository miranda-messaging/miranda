package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */

/**
 * A new node has connected.
 */
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
