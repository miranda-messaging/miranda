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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GetVersionsMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.networkMessages.GetFileWireMessage;
import com.ltsllc.miranda.node.networkMessages.GetVersionsWireMessage;
import com.ltsllc.miranda.node.networkMessages.JoinResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import org.apache.log4j.Logger;

/**
 * A node which has sent a join message enters this state and remains here
 * until it receives a reply.
 */
public class JoiningState extends NodeState {
    private Logger logger = Logger.getLogger(JoiningState.class);


    public JoiningState(Node node, Network network) throws MirandaException {
        super(node, network);
    }

    public State processMessage(Message m) throws MirandaException {
        State nextState = this;

        switch (m.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) m;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) m;
                nextState = processGetClusterFileMessage(getClusterFileMessage);
                break;
            }

            case GetVersions: {
                GetVersionsMessage getVersionsMessage = (GetVersionsMessage) m;
                nextState = processGetVersionsMessage(getVersionsMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }

        }

        return nextState;
    }

    public State processNetworkMessage(NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case JoinResponse: {
                JoinResponseWireMessage joinResponseWireMessage = (JoinResponseWireMessage) networkMessage.getWireMessage();
                nextState = processJoinResponse(joinResponseWireMessage);
                break;
            }

            case GetVersions: {
                GetVersionsWireMessage getVersionsWireMessage = (GetVersionsWireMessage) networkMessage.getWireMessage();
                nextState = processGetVersionsWireMessage(getVersionsWireMessage);
                break;
            }

            default: {
                nextState = super.processNetworkMessage(networkMessage);
                break;
            }
        }

        return nextState;
    }


    private State processGetClusterFileMessage(GetClusterFileMessage getClusterFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage(Cluster.NAME);
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetVersionsMessage(GetVersionsMessage getVersionsMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        return this;
    }


    private State processGetVersionsWireMessage(GetVersionsWireMessage getVersionsWireMessage) {
        GetVersionsMessage getVersionsMessage = new GetVersionsMessage(getNode().getQueue(), this);
        send(Miranda.getInstance().getQueue(), getVersionsMessage);

        return this;
    }

    private State processJoinResponse(JoinResponseWireMessage joinResponse) {
        State nextState = this;
        if (joinResponse.getResult() == JoinResponseWireMessage.Responses.Success) {
            logger.info("Successfully joined cluster");

            NodeReadyState nodeReadyState = new NodeReadyState(getNode(), getNetwork());
            nextState = nodeReadyState;
        } else {
            logger.warn("Failed to join cluster, closing connection");

            getNetwork().sendClose(getNode().getQueue(), this, getNode().getHandle());

            NodeStoppingState nodeStoppingState = new NodeStoppingState(getNode());
            nextState = nodeStoppingState;
        }

        return nextState;
    }
}
