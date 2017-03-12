package com.ltsllc.miranda.node.networkMessages;

import com.ltsllc.miranda.node.Node;

/**
 * Created by Clark on 2/6/2017.
 */
public class JoinWireMessage extends WireMessage {
    private String dns;
    private String ip;
    private int port;
    private String description;

    public JoinWireMessage (String dns, String ip, int port, String desciption) {
        super(WireSubjects.Join);

        this.dns = dns;
        this.ip = ip;
        this.port = port;
        this.description = description;
    }

    public JoinWireMessage (Node node) {
        super(WireSubjects.Join);

        this.dns = node.getDns();
        this.ip = node.getIp();
        this.port = node.getPort();
        this.description = node.getDescription();
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

    public String getDescription() {
        return description;
    }
}
