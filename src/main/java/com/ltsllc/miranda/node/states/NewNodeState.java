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

package com.ltsllc.miranda.node.states;

/**
 * Created by Clark on 2/7/2017.
 */

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.networkMessages.*;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * When a node is added via a connect it enters this state, waiting for a join message.
 */
public class NewNodeState extends NodeState {
    private static Logger logger = Logger.getLogger(NewNodeState.class);

    private Cluster cluster;

    public NewNodeState(Node node, Network network, Cluster cluster) throws MirandaException {
        super(node, network);

        this.cluster = cluster;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public static void setLogger(Logger logger) {
        NewNodeState.logger = logger;
    }

    public State processNetworkMessage(NetworkMessage networkMessage)  {
        State nextState = this;

        WireMessage wireMessage = networkMessage.getWireMessage();

        switch (wireMessage.getWireSubject()) {
            case Join: {
                JoinWireMessage joinWireMessage = (JoinWireMessage) networkMessage.getWireMessage();
                nextState = processJoinWireMessage(joinWireMessage);
                break;
            }

            case Versions: {
                VersionsWireMessage versionsWireMessage = (VersionsWireMessage) networkMessage.getWireMessage();
                nextState = processVersionsWireMessage(versionsWireMessage);
                break;
            }

            default:
                nextState = super.processNetworkMessage(networkMessage);
                break;
        }

        return nextState;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage(networkMessage);
                break;
            }


            case GetVersions: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) message;
                nextState = processConnectMessage(connectMessage);
                break;
            }


            case GetFile: {
                defer(message);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }



    private State processJoinWireMessage(JoinWireMessage joinWireMessage) {
        getNode().setDns(joinWireMessage.getDns());
        getNode().setPort(joinWireMessage.getPort());
        getNode().setDescription(joinWireMessage.getDescription());

        getCluster().sendNewNode(getNode().getQueue(), this, getNode());

        JoinResponseWireMessage joinResponseWireMessage = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Success);
        sendOnWire(joinResponseWireMessage);

        return new NodeReadyState(getNode(), getNetwork());
    }


    private State processGetVersionMessage(GetVersionMessage getVersionMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        return this;
    }

    private State processConnectMessage(ConnectMessage connectMessage) {
        String message = getNode() + " in state " + this + " was told to connect when it already has a connection!  "
                + " ignoring message.";

        logger.warn(message, connectMessage.getWhere());

        return getNode().getCurrentState();
    }


    private State processVersionsWireMessage(VersionsWireMessage versionsWireMessage) {
        VersionsMessage versionsMessage = new VersionsMessage(getNode().getQueue(), this, versionsWireMessage.getVersions());
        send(Miranda.getInstance().getQueue(), versionsMessage);

        return this;
    }



 }