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
public class NodesLoaded extends Message {
    private List<NodeElement> nodes;

    public NodesLoaded (List<NodeElement> nodes, BlockingQueue<Message> sender) {
        super(Subjects.NodesLoaded, sender);
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
