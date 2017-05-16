package com.ltsllc.miranda.servlet.status;

import com.ltsllc.miranda.node.NodeElement;

/**
 * Created by Clark on 3/10/2017.
 */
public class NodeStatus extends NodeElement {
    public enum NodeStatuses {
        Online,
        Offline
    }

    private NodeStatuses nodeStatus;

    public NodeStatuses getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatuses nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public NodeStatus (String dns, String ip, int port, String descrition, NodeStatuses nodeStatus) {
        super(dns, ip, port, descrition);

        setNodeStatus(nodeStatus);
    }
}
