package com.ltsllc.miranda.node;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.network.ConnectToMessage;
import com.ltsllc.miranda.network.SendMessageMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Node extends Consumer
{
    public Node(NodeElement element, BlockingQueue<Message> network) {
        super("node");
        dns = element.getDns();
        ip = element.getIp();
        port = element.getPort();
        this.network = network;
        NodeStartState nodeStartState = new NodeStartState(this, getNetwork());
        setCurrentState(nodeStartState);
    }

    public Node (InetSocketAddress address, int handle) {
        super("node");

        if (null != address) {
            dns = address.getHostName();
            ip = address.toString();
            port = address.getPort();
        }

        this.handle = handle;

        State connectedState = new ConnectedState(this);
        setCurrentState(connectedState);
    }


    public Node (int handle)
    {
        super("node");

        this.handle = handle;
        setCurrentState(new NewNodeState(this));
    }

    public Node() {
        super("node");
    }


    private static Logger logger = Logger.getLogger(Node.class);

    private String dns;
    private String ip;
    private String description;
    private int port;
    private BlockingQueue<Message> network;
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

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public void setNetwork(BlockingQueue<Message> network) {
        this.network = network;
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

    public void connect (BlockingQueue<Message> senderQueue, Object sender) {
        ConnectMessage connectMessage = new ConnectMessage(senderQueue, sender, getDns(), getPort());
        send (connectMessage, getQueue());
    }

    public void connect () {
        ConnectToMessage connectToMessage = new ConnectToMessage(getDns(), getPort(), getQueue(), this);
        send (connectToMessage, getNetwork());

        ConnectingState connectingState = new ConnectingState(this);
        setCurrentState(connectingState);
    }

    public void sendOnWire(WireMessage wireMessage) {
        String json = wireMessage.getJson();
        byte[] buffer = json.getBytes();

        SendMessageMessage message = new SendMessageMessage(getQueue(), this, getHandle(), buffer);
        send(message, getNetwork());
    }


    public void sync () {
        SyncingState syncingState = new SyncingState(this);
        setCurrentState(syncingState);
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
        return nodeElement;
    }
}
