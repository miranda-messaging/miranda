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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.*;
import org.apache.log4j.Logger;

/**
 * Waiting for a response to a {@link com.ltsllc.miranda.node.networkMessages.StopWireMessage}
 * <p>
 * <p>
 * This state assumes that a {@link com.ltsllc.miranda.node.networkMessages.StopWireMessage}
 * has already been sent.
 * </p>
 * <p>
 * NOTE THAT WHILE IN THIS STATE MOST MESSAGES WILL BE DISCARDED!
 * </p>
 */
public class NodeDisconnectingState extends State {
    private static Logger logger = Logger.getLogger(NodeDisconnectingState.class);

    public Node getNode() {
        return (Node) getContainer();
    }

    public NodeDisconnectingState(Node node) throws MirandaException {
        super(node);
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

            default: {
                nextState = discardMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processNetworkMessage(NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case StopResponse: {
                StopResponseWireMessage stopResponseWireMessage = (StopResponseWireMessage) networkMessage.getWireMessage();
                nextState = processStopResponseWireMessage(stopResponseWireMessage);
                break;
            }

            case Stop: {
                StopWireMessage stopWireMessage = (StopWireMessage) networkMessage.getWireMessage();
                nextState = processStopWireMessage(stopWireMessage);
                break;
            }

            default: {
                nextState = discardWireMessage(networkMessage.getWireMessage());
                break;
            }
        }

        return nextState;
    }

    public State processStopResponseWireMessage(StopResponseWireMessage stopResponseWireMessage) {
        logger.info(getNode() + " got stop response.");

        getNode().getNetwork().sendCloseMessage(getNode().getQueue(), this, getNode().getHandle());

        return new NodeStoppingState(getNode());
    }

    public State processStopWireMessage(StopWireMessage stopWireMessage) {
        StopResponseWireMessage response = new StopResponseWireMessage();
        getNode().getNetwork().sendNetworkMessage(getNode().getQueue(), this, getNode().getHandle(), response);

        return this;
    }

    public State discardMessage(Message message) {
        logger.warn(getNode() + " is discarding " + message);

        return this;
    }

    public State discardWireMessage(WireMessage wireMessage) {
        logger.warn(getNode() + " is discarding a network message " + wireMessage);

        StoppingWireMessage stoppingWireMessage = new StoppingWireMessage();
        getNode().getNetwork().sendNetworkMessage(getNode().getQueue(), this, getNode().getHandle(), stoppingWireMessage);

        return this;
    }
}
