package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.ConnectToMessage;
import com.ltsllc.miranda.timer.TimeoutMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/22/2017.
 */
public class RetryingState extends State {
    private Logger logger = Logger.getLogger(RetryingState.class);

    private Node node;

    public Node getNode() {
        return node;
    }

    public RetryingState (Node node) {
        super(node);
        this.node = node;
    }

    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case Timeout: {
                TimeoutMessage timeoutMessage = (TimeoutMessage) m;
                nextState = prcessTimeoutMessage(timeoutMessage);
                break;
            }
        }

        return nextState;
    }

    private State prcessTimeoutMessage (TimeoutMessage timeoutMessage) {
        logger.info ("Retrying " + getNode().getDns() + ":" + getNode().getPort());

        ConnectToMessage connectToMessage = new ConnectToMessage(getNode().getDns(), getNode().getPort(), getNode().getQueue(), this);
        send (getNode().getNetwork(), connectToMessage);
        return new ConnectingState(getNode());
    }
}
