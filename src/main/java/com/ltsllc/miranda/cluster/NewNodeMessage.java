package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class NewNodeMessage extends Message {
    private Node node;
    private String dns;
    private int port;
    private String ip;
    private String description;

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

    /*
    public NewNodeMessage (BlockingQueue<Message> senderQueue, Object sender, String nodeDns, String nodeIp, int nodePort, String nodeDescription) {
        super(Subjects.NewNode, senderQueue, sender);
        this.description = nodeDescription;
        this.dns = nodeDns;
        this.ip = nodeIp;
        this.port = nodePort;
    }

    public NewNodeMessage (BlockingQueue<Message> senderQueue, Object sender, NewNodeMessage newNodeMessage) {
        super(Subjects.NewNode, senderQueue, sender);
        this.dns = newNodeMessage.dns;
        this.ip = newNodeMessage.ip;
        this.port = newNodeMessage.port;
        this.description = newNodeMessage.description;
    }
    */

    public Node getNode() {
        return node;
    }

    public NewNodeMessage (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        super(Subjects.NewNode, senderQueue, sender);
        this.node = node;
    }
}
