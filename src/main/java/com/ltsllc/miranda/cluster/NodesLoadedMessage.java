package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/20/2017.
 */
public class NodesLoadedMessage extends Message {
    private List<NodeElement> nodes;

    public NodesLoadedMessage(List<NodeElement> nodes, BlockingQueue<Message> sender, Object senderObject) {
        super(Subjects.NodesLoaded, sender, senderObject);
        this.nodes = nodes;
    }

    public List<NodeElement> getNodes() {
        if (nodes == null)
        {
            nodes = new ArrayList<NodeElement>();
        }
        return nodes;
    }

    public String toString () {
        return "nodes loaded";
    }
}
