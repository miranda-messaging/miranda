package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.messages.CloseMessage;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.network.messages.SendMessageMessage;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/29/2017.
 */
public class NetworkReadyState extends State {
    private static Logger logger = Logger.getLogger (NetworkReadyState.class);

    private Network network;

    public NetworkReadyState(Network network) {
        super(null);
        this.network = network;
    }

    public Network getNetwork() {
        return network;
    }

    public State processMessage (Message m)
    {
        State nextState = this;

        switch (m.getSubject())
        {
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
                CloseMessage disconnectMessage = (CloseMessage) m;
                nextState = processDisconectMessage(disconnectMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }
        }

        return nextState;
    }


    private State processConnectToMessage (ConnectToMessage connectToMessage) {
        State nextState = this;

        logger.info ("Connecting to " + connectToMessage.getHost() + ":" + connectToMessage.getPort());

        getNetwork().connect(connectToMessage);

        return nextState;
    }


    private State processDisconectMessage (CloseMessage disconnectMessage) {
        getNetwork().disconnect(disconnectMessage);

        return this;
    }

    private State processSendNetworkMessage (SendNetworkMessage sendNetworkMessage) {
        getNetwork().sendNetworkMessage(sendNetworkMessage);

        return this;
    }
}
