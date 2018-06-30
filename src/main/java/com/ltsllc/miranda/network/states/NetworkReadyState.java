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

package com.ltsllc.miranda.network.states;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.messages.*;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/29/2017.
 */
public class NetworkReadyState extends State {
    private static Logger logger = Logger.getLogger(NetworkReadyState.class);

    public NetworkReadyState(Network network) throws MirandaException {
        super(network);
    }

    public Network getNetwork() {
        return (Network) container;
    }

    public State processMessage(Message m) throws MirandaException {
        State nextState = this;

        switch (m.getSubject()) {
            case ConnectTo: {
                ConnectToMessage connectToMessage = (ConnectToMessage) m;
                nextState = processConnectToMessage(connectToMessage);
                break;
            }

            case SendNetworkMessage: {
                SendNetworkMessage sendNetworkMessage = (SendNetworkMessage) m;
                nextState = processSendNetworkMessage(sendNetworkMessage);
                break;
            }

            case Disconnect: {
                CloseMessage closeMessage = (CloseMessage) m;
                nextState = processCloseMessage(closeMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }
        }

        return nextState;
    }


    private State processConnectToMessage(ConnectToMessage connectToMessage) throws MirandaException {
        State nextState = this;

        logger.info("Connecting to " + connectToMessage.getHost() + ":" + connectToMessage.getPort());

        getNetwork().connect(connectToMessage);

        return nextState;
    }


    private State processCloseMessage(CloseMessage closeMessage) throws MirandaException {
        getNetwork().disconnect(closeMessage);

        CloseResponseMessage reply = new CloseResponseMessage(getNetwork().getQueue(), this, closeMessage.getHandle(),
                Results.Success);
        closeMessage.reply(reply);

        return this;
    }

    private State processSendNetworkMessage(SendNetworkMessage sendNetworkMessage) throws MirandaException {
        try {
            getNetwork().sendOnNetwork(sendNetworkMessage);
        } catch (NetworkException e) {
            if (e.getError() == NetworkException.Errors.UnrecognizedHandle) {
                UnknownHandleMessage unknownHandleMessage = new UnknownHandleMessage(getNetwork().getQueue(), this, sendNetworkMessage.getHandle());
                sendNetworkMessage.reply(unknownHandleMessage);
            } else {
                NetworkErrorMessage networkErrorMessage = new NetworkErrorMessage(getNetwork().getQueue(), this, e);
                sendNetworkMessage.reply(networkErrorMessage);
            }
        }

        return this;
    }
}
