package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.network.messages.DisconnectedMessage;
import com.ltsllc.miranda.node.Node;
import org.apache.log4j.Logger;

/**
 * A cluster node that is shutting down.
 *
 * <p>
 *     A Node enters this state when it is the process of shutting down.  It
 *     assumes that a {@link com.ltsllc.miranda.network.messages.CloseMessage}
 *     has already been sent so it waits for a response.
 * </p>
 *
 * <p>
 *     WHEN IN THIS STATE THE NODE WILL DISCARD ANY MESSAGE OTHER THAN A
 *     DISCONNECTED MESSAGE FROM THE NETWORK!
 * </p>
 */
public class NodeStoppingState extends State {
    private static Logger logger = Logger.getLogger(NodeStoppingState.class);

    public Node getNode() {
        return (Node) getContainer();
    }

    public NodeStoppingState (Node node) {
        super(node);
    }

    public State processMessage (Message message) {
        State nextState = this;

        switch (message.getSubject())
        {
            case Disconnected: {
                DisconnectedMessage disconnectedMessage = (DisconnectedMessage) message;
                nextState = processDisconntedMessage(disconnectedMessage);
                break;
            }

            default: {
                nextState = discardMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processDisconntedMessage(DisconnectedMessage disconnectedMessage) {
        logger.info(getNode() + " disconnected, stopping");
        getNode().getCluster().sendNodeStopped(getNode().getQueue(), this, getNode());

        return StopState.getInstance();
    }

    public State discardMessage (Message message) {
        logger.warn (getNode() + " is discarding a message " + message);

        return this;
    }
}
