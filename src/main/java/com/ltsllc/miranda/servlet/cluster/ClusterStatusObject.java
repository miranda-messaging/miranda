package com.ltsllc.miranda.servlet.cluster;

import com.ltsllc.miranda.servlet.status.NodeStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 3/10/2017.
 */
public class ClusterStatusObject {
    private List<NodeStatus> nodes = new ArrayList<NodeStatus>();

    public List<NodeStatus> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeStatus> nodes) {
        this.nodes = nodes;
    }

    public ClusterStatusObject (List<NodeStatus> nodes) {
        this.nodes = new ArrayList<NodeStatus>(nodes);
    }
}
