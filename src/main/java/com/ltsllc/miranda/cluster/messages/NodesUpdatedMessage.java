package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NodeElement;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/11/2017.
 */
public class NodesUpdatedMessage extends Message {
    private List<NodeElement> nodeList;

    public List<NodeElement> getNodeList() {
        return nodeList;
    }

    public NodesUpdatedMessage (BlockingQueue<Message> senderQueue, Object sender, List<NodeElement> nodeList) {
        super(Subjects.NodesUpdated, senderQueue, sender);

        this.nodeList = nodeList;
    }
}
