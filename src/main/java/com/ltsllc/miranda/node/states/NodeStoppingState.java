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
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.network.messages.CloseResponseMessage;
import com.ltsllc.miranda.network.messages.DisconnectedMessage;
import com.ltsllc.miranda.node.Node;
import org.apache.log4j.Logger;

/**
 * A cluster node that is shutting down.
 * <p>
 * <p>
 * A Node enters this state when it is the process of shutting down.  It
 * assumes that a {@link com.ltsllc.miranda.network.messages.CloseMessage}
 * has already been sent so it waits for a response.
 * </p>
 * <p>
 * <p>
 * WHEN IN THIS STATE THE NODE WILL DISCARD ANY MESSAGE OTHER THAN A
 * DISCONNECTED MESSAGE FROM THE NETWORK!
 * </p>
 */
public class NodeStoppingState extends State {
    private static Logger logger = Logger.getLogger(NodeStoppingState.class);

    public Node getNode() {
        return (Node) getContainer();
    }

    public NodeStoppingState(Node node) throws MirandaException {
        super(node);
    }

    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case CloseResponse: {
                CloseResponseMessage closeResponseMessage = (CloseResponseMessage) message;
                nextState = processCloseResponseMessage(closeResponseMessage);
                break;
            }

            default: {
                nextState = discardMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCloseResponseMessage(CloseResponseMessage closeResponseMessage) {
        if (closeResponseMessage.getResult() != Results.Success) {
            logger.error("Got result " + closeResponseMessage.getResult() + " shutting down.  Continuing with shutdown");
        }

        String name = getNode().getDns() + ":" + getNode().getPort();
        getNode().getCluster().sendShutdownResponse(getNode().getQueue(), this, name);

        return StopState.getInstance();
    }

    public State discardMessage(Message message) {
        logger.warn(getNode() + " is shutting down, discarding a message " + message);

        return this;
    }

    public State processDisconnectedMessage(DisconnectedMessage disconnectedMessage) {
        String name = getNode().getDns() + ":" + getNode().getPort();
        getNode().getCluster().sendShutdownResponse(getNode().getQueue(), this, name);

        return StopState.getInstance();
    }
}
