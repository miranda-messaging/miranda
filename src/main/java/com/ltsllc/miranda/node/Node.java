/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.clientinterface.objects.NodeStatus;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.networkMessages.GetVersionWireMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.operations.syncfiles.messages.GetFileWireMessage;
import com.ltsllc.miranda.node.networkMessages.StopWireMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.node.states.ConnectingState;
import com.ltsllc.miranda.node.states.NodeIncomingStartState;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Node extends Consumer {
    public Node()
    {
        super("node");
        description = "The local system";
    }

    public Node(NodeElement element, Network network, Cluster cluster) throws MirandaException {
        super("node");
        dns = element.getDns();
        port = element.getPort();
        description = element.getDescription();
        this.network = network;
        this.cluster = cluster;

        ConnectingState connectingState = new ConnectingState(this, network);
        setCurrentState(connectingState);
    }

    /**
     * This constructor is used when a new cluster node connects to us.
     *
     * @param handle
     */
    public Node(int handle, Network network, Cluster cluster) throws MirandaException {
        super("node");

        this.handle = handle;
        this.cluster = cluster;
        this.network = network;

        State nodeIncomingState = new NodeIncomingStartState(this, network, cluster);
        setCurrentState(nodeIncomingState);
    }


    private static Logger logger = Logger.getLogger(Node.class);

    private String dns;
    private String description;
    private int port;
    private Network network;
    private int handle = -1;
    private Cluster cluster;


    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Cluster getCluster() {
        return cluster;
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

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public boolean equalsElement(NodeElement nodeElement) {
        if (null == getDns())
            return false;

        return getDns().equals(nodeElement.getDns()) && getPort() == nodeElement.getPort();
    }

    public void connect() {
        getNetwork().sendConnect(getQueue(), this, getDns(), getPort());
    }

    public void sendOnWire(WireMessage wireMessage) {
        getNetwork().sendMessage(getQueue(), this, getHandle(), wireMessage);
    }


    public boolean isConnected() {
        return -1 != handle;
    }


    public NodeElement getUpdatedElement() {
        NodeElement nodeElement = new NodeElement(getDns(), getPort(), getDescription());
        Date date = new Date();
        nodeElement.setLastConnected(date.getTime());
        return nodeElement;
    }

    public NodeElement getNodeElement() {
        NodeElement nodeElement = new NodeElement(getDns(), getPort(), getDescription());

        if (isConnected()) {
            nodeElement.setLastConnected(System.currentTimeMillis());
        }

        return nodeElement;
    }


    public boolean matches(NodeElement nodeElement) {
        return getDns().equals(nodeElement.getDns()) && getPort() == nodeElement.getPort();

    }

    public NodeElement asNodeElement() {
        NodeElement nodeElement = new NodeElement(getDns(), getPort(), getDescription());

        if (isConnected()) {
            nodeElement.setLastConnected(System.currentTimeMillis());
        }

        return nodeElement;
    }

    public NodeStatus getStatus() {
        NodeStatus.NodeStatuses status = isConnected() ? NodeStatus.NodeStatuses.Online : NodeStatus.NodeStatuses.Offline;
        NodeStatus nodeStatus = new NodeStatus(getDns(), getPort(), getDescription(), status);
        return nodeStatus;
    }

    public void stop() {
        StopWireMessage stopWireMessage = new StopWireMessage();
        getNetwork().sendNetworkMessage(getQueue(), this, getHandle(), stopWireMessage);
    }

    public void sendSendNetworkMessage(BlockingQueue<Message> senderQueue, Object sender, WireMessage wireMessage) {
        SendNetworkMessage sendNetworkMessage = new SendNetworkMessage(senderQueue, sender, wireMessage, getHandle());
        sendToMe(sendNetworkMessage);
    }

    public void disconnect() {
        if (isConnected()) {
            getNetwork().sendClose(getQueue(), this, getHandle());
        }
    }

    public void downloadFile (Files file) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage(file);
        sendOnWire(getFileWireMessage);
    }

    public void sendGetVersionMessage () {
        GetVersionWireMessage getVersionWireMessage = new GetVersionWireMessage();
        sendOnWire(getVersionWireMessage);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Node { ");
        stringBuilder.append(getDns());
        stringBuilder.append(", ");
        stringBuilder.append(getPort());
        stringBuilder.append(", ");
        stringBuilder.append(getHandle());
        stringBuilder.append("}");

        return stringBuilder.toString();
    }


    public static Node LOCAL = new LocalNode();

    public void sendGetFile (BlockingQueue senderQueue, Object sender, Files file) {
        GetFileMessage getFileMessage = new GetFileMessage(senderQueue, sender, file);
        sendToMe (getFileMessage);
    }
}
