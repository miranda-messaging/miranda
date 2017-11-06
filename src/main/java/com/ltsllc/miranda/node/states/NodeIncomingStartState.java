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
import com.ltsllc.miranda.network.ConnectionListener;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.JoinResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;

/**
 * A node enters this state when we get a new connection from the {@link ConnectionListener}.
 *
 * <p>
 *     We are waiting for a join message.
 * </p>
 */
public class NodeIncomingStartState extends NodeState {
    private Cluster cluster;

    public Cluster getCluster() {
        return cluster;
    }

    public NodeIncomingStartState (Node node, Network network, Cluster cluster) throws MirandaException {
        super (node, network);

        this.cluster = cluster;
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    @Override
    public State processNetworkMessage(NetworkMessage networkMessage) throws MirandaException {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case Join: {
                JoinWireMessage joinMessage = (JoinWireMessage) networkMessage.getWireMessage();
                nextState = processJoinWireMessage(joinMessage);
                break;
            }

            default: {
                nextState = super.processNetworkMessage(networkMessage);
                break;
            }
        }

        return nextState;
    }

    /**
     * The remote node sent a join message.
     *
     * <p>
     *     Add ourselves to the cluster, send a response, and transition to the
     *     ready state.
     * </p>
     *
     * @param joinWireMessage The join message
     * @return The next state.  In normal circumstances, this should be the ready
     * state.
     */
    private State processJoinWireMessage (JoinWireMessage joinWireMessage) throws MirandaException {
        getNode().setDns(joinWireMessage.getDns());
        getNode().setPort(joinWireMessage.getPort());
        getNode().setDescription(joinWireMessage.getDescription());

        getCluster().newNode(getNode());

        JoinResponseWireMessage message = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Success);
        sendOnNetwork(message);

        NodeReadyState readyState = new NodeReadyState(getNode(), getNetwork());
        return readyState;
    }
}
