package com.ltsllc.miranda.node;

/**
 * Created by Clark on 1/21/2017.
 */
public class NodeElement {
    private String dns;
    private String ip;
    private int port;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
