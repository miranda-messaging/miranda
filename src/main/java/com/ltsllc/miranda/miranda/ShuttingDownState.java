package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.deliveries.DeliveryManager;
import com.ltsllc.miranda.event.EventManager;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.user.UserManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Miranda is shutting down.  When it enters this state, it is waiting for
 * a ready message from each of its subsystems.  When it gets those it terminates.
 */
public class ShuttingDownState extends State {
    private static Logger logger = Logger.getLogger(ShuttingDownState.class);

    public static void setLogger (Logger logger) {
        ShuttingDownState.logger = logger;
    }

    public Miranda getMiranda () {
        return (Miranda) getContainer();
    }

    public State processMessage (Message message) {
        State nextState = getMiranda().getCurrentState();

        switch (message.getSubject()) {
            case ShutdownResponse: {
                ShutdownResponseMessage shutdownResponseMessage = (ShutdownResponseMessage) message;
                nextState = processShutdownResponseMessage (shutdownResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }


    public ShuttingDownState (Miranda miranda) {
        super(miranda);
    }

    public State processShutdownResponseMessage (ShutdownResponseMessage shutdownResponseMessage) {
        getMiranda().subsystemShutDown(shutdownResponseMessage.getName());

        if (getMiranda().readyToShutDown()) {
            logger.info ("System is shutting down");
            return StopState.getInstance();
        }

        return getMiranda().getCurrentState();
    }

}
