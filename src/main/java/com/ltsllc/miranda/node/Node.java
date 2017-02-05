package com.ltsllc.miranda.node;

import com.ltsllc.miranda.*;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
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
        StartingState startingState = new StartingState(this);
        setCurrentState(startingState);
        this.network = network;
    }

    public Node (InetSocketAddress address) {
        super("node");

        if (null != address) {
            dns = address.getHostName();
            ip = address.toString();
            port = address.getPort();
        }

        setCurrentState(new ConnectedState(this));
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
    private Channel channel;


    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public State processMessage(Message m) {
        return super.processMessage(m);
    }
}
