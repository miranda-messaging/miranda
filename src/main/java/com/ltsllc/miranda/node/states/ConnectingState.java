package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.messages.ConnectFailedMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.node.messages.RetryMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/21/2017.
 */
public class ConnectingState extends NodeState {
    private Logger logger = Logger.getLogger(ConnectingState.class);

    public ConnectingState (Node node, Network network) {
        super(node, network);
    }


    public State processMessage (Message m) {
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


    private State processConnectSucceededMessage (ConnectSucceededMessage connectSucceededMessage) {
        logger.info("got connection");

        getNode().setHandle(connectSucceededMessage.getHandle());

        JoinWireMessage joinWireMessage = new JoinWireMessage(getNode());
        sendOnWire(joinWireMessage);

        return new JoiningState(getNode(), getNetwork());
    }


    private State processConnectFailedMessage (ConnectFailedMessage connectFailedMessage) {
        String message = "Failed to get connection to " + getNode().getDns() + ":" + getNode().getPort();
        logger.info(message, connectFailedMessage.getCause());

        return new RetryingState(getNode(), getNetwork());
    }
}
