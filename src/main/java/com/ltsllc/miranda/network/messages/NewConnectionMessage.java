package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */
public class NewConnectionMessage extends Message {
    private Node node;

    public Node getNode() {
        return node;
    }

    public NewConnectionMessage (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        super(Subjects.NewConnection, senderQueue, sender);

        this.node = node;
    }
}
