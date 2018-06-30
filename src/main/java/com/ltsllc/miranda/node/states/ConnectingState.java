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
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.ConnectFailedMessage;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/21/2017.
 */
public class ConnectingState extends NodeState {
    private Logger logger = Logger.getLogger(ConnectingState.class);

    public ConnectingState(Node node, Network network) throws MirandaException {
        super(node, network);
    }


    public State processMessage(Message m) throws MirandaException {
        State nextState = this;

        switch (m.getSubject()) {
            case ConnectSucceeded: {
                ConnectSucceededMessage connectSucceededMessage = (ConnectSucceededMessage) m;
                nextState = processConnectSucceededMessage(connectSucceededMessage);
                break;
            }

            case ConnectFailed: {
                ConnectFailedMessage connectFailedMessage = (ConnectFailedMessage) m;
                nextState = processConnectFailedMessage(connectFailedMessage);
                break;
            }
            default: {
                nextState = super.processMessage(m);
                break;
            }
        }

        return nextState;
    }


    private State processConnectSucceededMessage(ConnectSucceededMessage connectSucceededMessage) throws MirandaException {
        logger.info("got connection");

        getNode().setHandle(connectSucceededMessage.getHandle());

        JoinWireMessage joinWireMessage = new JoinWireMessage(getNode());
        sendOnWire(joinWireMessage);

        return new JoiningState(getNode(), getNetwork());
    }


    private State processConnectFailedMessage(ConnectFailedMessage connectFailedMessage) throws MirandaException {
        String message = "Failed to get connection to " + getNode().getDns() + ":" + getNode().getPort();
        logger.info(message, connectFailedMessage.getCause());

        return new RetryingState(getNode(), getNetwork());
    }
}
