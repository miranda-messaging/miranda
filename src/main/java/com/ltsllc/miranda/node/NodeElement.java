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

    public NodeElement (String dns, String ip, int port) {
        this.dns = dns;
        this.ip = ip;
        this.port = port;
    }

    public boolean equals(NodeElement element) {
        return dns.equals(element.getDns()) && ip.equals(element.getIp()) && port == element.getPort();
    }
}
