package com.ltsllc.miranda.node;

import java.util.Date;

/**
 * Created by Clark on 1/21/2017.
 */
public class NodeElement {
    private String dns;
    private String ip;
    private int port;
    private String description;
    private long lastConnected;

    public long getLastConnected() {
        return lastConnected;
    }

    public void setLastConnected(long lastConnected) {
        this.lastConnected = lastConnected;
    }

    public String getDescription() {
        return description;
    }

    public String getDns() {
        return dns;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NodeElement (Node node) {
        this.dns = node.getDns();
        this.ip = node.getIp();
        this.port = node.getPort();
    }

    public NodeElement (String dns, String ip, int port, String description) {
        this.dns = dns;
        this.ip = ip;
        this.port = port;
        this.description = description;
    }

    public boolean equals(NodeElement element) {
        return dns.equals(element.getDns()) && ip.equals(element.getIp()) && port == element.getPort();
    }

    public boolean hasTimedout (long timeout) {
        Date date = new Date();
        long now = date.getTime();
        long timeSinceLastConnect = now - getLastConnected();
        return timeSinceLastConnect > timeout;
    }
}
