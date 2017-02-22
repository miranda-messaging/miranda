package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NodeElement;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */
public class DropNodeMessage extends Message {
    private NodeElement droppedNode;

    public DropNodeMessage (BlockingQueue<Message> senderQueue, Object sender, NodeElement droppedNode) {
        super(Subjects.DropNode, senderQueue, sender);

        this.droppedNode = droppedNode;
    }

    public NodeElement getDroppedNode() {
        return droppedNode;
    }
}
