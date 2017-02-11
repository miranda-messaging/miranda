package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.Node;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class SynchronizeMessage extends Message {
    private Node node;

    public SynchronizeMessage (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        super(Subjects.Synchronize, senderQueue, sender);

        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
