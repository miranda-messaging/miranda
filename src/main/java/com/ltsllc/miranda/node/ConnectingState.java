package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.network.ConnectedMessage;
import com.ltsllc.miranda.timer.SchedulePeriodicMessage;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/21/2017.
 */
public class ConnectingState extends NodeState {
    private Logger logger = Logger.getLogger(ConnectingState.class);

    public ConnectingState (Node node) {
        super(node);
    }


    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case Connected: {
                ConnectedMessage connectedMessage = (ConnectedMessage) m;
                nextState = processConnectedMessage(connectedMessage);
                break;
            }

            case ConnectionError: {
                ConnectFailedMessage connectFailedMessage = (ConnectFailedMessage) m;
                nextState = processConnectFailed(connectFailedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }

        }

        return nextState;
    }


    private State processConnectedMessage (ConnectedMessage connectedMessage) {
        logger.info("got connection to " + connectedMessage.getChannel().remoteAddress());

        getNode().setChannel(connectedMessage.getChannel());

        JoinWireMessage joinWireMessage = new JoinWireMessage(getNode());
        sendOnWire(joinWireMessage);

        return new JoiningState(getNode());
    }


    private State processConnectFailed (ConnectFailedMessage connectFailedMessage) {
        String message = "Failed to get connection to " + getNode().getDns() + ":" + getNode().getPort();
        logger.info(message, connectFailedMessage.getCause());

        MirandaProperties p = Miranda.properties;
        int delayBetweenRetries = PropertiesUtils.getIntProperty(System.getProperties(), MirandaProperties.PROPERTY_DELAY_BETWEEN_RETRIES);
        RetryMessage retryMessage = new RetryMessage(getNode().getQueue(), this);
        SchedulePeriodicMessage schedulePeriodicMessage = new SchedulePeriodicMessage(getNode().getQueue(), this, retryMessage, delayBetweenRetries);
        send(Miranda.timer.getQueue(), schedulePeriodicMessage);

        return new RetryingState(getNode());
    }

}
