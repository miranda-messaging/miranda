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

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/29/2017.
 */
public class NodeState extends State {
    private Logger logger = Logger.getLogger(NodeState.class);

    private Network network;

    public Node getNode() {
        return (Node) getContainer();
    }

    public Network getNetwork() {
        return network;
    }

    public NodeState (Node node, Network network) {
        super(node);

        this.network = network;
    }

    public void sendOnWire (WireMessage wireMessage) {
        getNetwork().sendNetworkMessage(getNode().getQueue(), this, getNode().getHandle(), wireMessage);
    }

    public State processNetworkMessage (NetworkMessage networkMessage) {
        String message = this + " does but understand network message " + networkMessage.getWireMessage().getWireSubject();
        logger.error (message);
        logger.error ("message created at", networkMessage.getWhere());
        Panic panic = new Panic(message, Panic.Reasons.DoesNotUnderstandNetworkMessage);
        Miranda.getInstance().panic(panic);

        return getNode().getCurrentState();
    }

    public void sendOnNetwork (WireMessage wireMessage) {
        getNetwork().sendMessage(getNode().getQueue(), this, getNode().getHandle(), wireMessage);
    }
}
