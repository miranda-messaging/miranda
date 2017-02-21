package com.ltsllc.miranda.node;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

import java.util.Date;

/**
 * Created by Clark on 1/21/2017.
 */
public class NodeElement implements Perishable {
    private static Gson ourGson = new Gson();

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
        return timeSinceLastConnect >= timeout;
    }

    /**
     * false.
     *
     * Objects of this class do not expire.  Instead, during a health check,
     * an element may have not connected in an acceptable time frame and
     * therefore get dropped.
     *
     * @param time
     * @return
     */
    public boolean expired (long time) {
        return false;
    }


    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ourGson.toJson(this));

        return stringBuffer.toString();
    }


    public void update (NodeElement newValue) {
        this.dns = newValue.dns;
        this.ip = newValue.ip;
        this.port = newValue.port;
        this.description = newValue.description;
    }


    public String toJson() {
        return ourGson.toJson(this);
    }
}
