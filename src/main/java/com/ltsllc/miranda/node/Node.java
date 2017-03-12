package com.ltsllc.miranda.node;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.node.states.NewNodeState;
import com.ltsllc.miranda.node.states.NodeStartState;
import com.ltsllc.miranda.node.states.SyncingState;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Node extends Consumer
{
    public Node(NodeElement element, Network network) {
        super("node");
        dns = element.getDns();
        ip = element.getIp();
        port = element.getPort();
        this.network = network;

        NodeStartState nodeStartState = new NodeStartState(this, getNetwork());
        setCurrentState(nodeStartState);
    }

    /**
     * This constructor is used when a new cluster node connects to us.
     *
     * @param handle
     */
    public Node (int handle, Network network, Cluster cluster) {
        super("node");


        this.handle = handle;

        State newNodeState = new NewNodeState(this, network, cluster);
        setCurrentState(newNodeState);
    }


    private static Logger logger = Logger.getLogger(Node.class);

    private String dns;
    private String ip;
    private String description;
    private int port;
    private Network network;
    private int handle = -1;


    public String getDns() {
        return dns;
    }

    public void setDns (String dns) {
        this.dns = dns;
    }

    public String getIp() {
        return ip;
    }

    public void setIp (String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Network getNetwork() {
        return network;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle (int handle) {
        this.handle = handle;
    }

    public boolean equalsElement (NodeElement nodeElement) {
        if (null == getDns())
            return false;

        return getDns().equals(nodeElement.getDns()) && getIp().equals(nodeElement.getIp()) && getPort() == nodeElement.getPort();
    }

    public void connect () {
        getNetwork().sendConnect (getQueue(), this, getDns(), getPort());
    }

    public void sendOnWire(WireMessage wireMessage) {
        getNetwork().sendMessage(getQueue(), this, getHandle(), wireMessage);
    }


    public boolean isConnected() {
        return -1 != handle;
    }


    public NodeElement getUpdatedElement () {
        NodeElement nodeElement = new NodeElement(getDns(), getIp(), getPort(), getDescription());
        Date date = new Date();
        nodeElement.setLastConnected(date.getTime());
        return nodeElement;
    }

    public NodeElement getNodeElement() {
        NodeElement nodeElement = new NodeElement(getDns(), getIp(), getPort(), getDescription());

        if (isConnected()) {
            nodeElement.setLastConnected(System.currentTimeMillis());
        }

        return nodeElement;
    }


    public boolean matches (NodeElement nodeElement) {
        return (
                getDns().equals(nodeElement.getDns())
                        && getIp().equals(nodeElement.getIp())
                        && getPort() == nodeElement.getPort()
        );
    }

    public NodeElement asNodeElement () {
        NodeElement nodeElement = new NodeElement(getDns(), getIp(), getPort(), getDescription());

        if (isConnected()) {
            nodeElement.setLastConnected(System.currentTimeMillis());
        }

        return nodeElement;
    }
}
