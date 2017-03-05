package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.node.NodeElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 3/4/2017.
 */
public class StatusObject {
    private List<NodeElement> cluster = new ArrayList<NodeElement>();
    private List<Property> properties = new ArrayList<Property>();
    private NodeElement local;

    public List<NodeElement> getCluster() {
        return cluster;
    }

    public void setCluster(List<NodeElement> cluster) {
        this.cluster = cluster;
    }

    public NodeElement getLocal() {
        return local;
    }

    public StatusObject (NodeElement local, List<Property> properties, List<NodeElement> cluster) {
        this.local = local;
        this.properties = properties;
        this.cluster = cluster;
    }
}
