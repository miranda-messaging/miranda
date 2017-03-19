package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.*;
import org.apache.log4j.Logger;

/**
 * Waiting for a response to a {@link com.ltsllc.miranda.node.networkMessages.StopWireMessage}
 *
 * <p>
 *     This state assumes that a {@link com.ltsllc.miranda.node.networkMessages.StopWireMessage}
 *     has already been sent.
 * </p>
 * <p>
 *     NOTE THAT WHILE IN THIS STATE MOST MESSAGES WILL BE DISCARDED!
 * </p>
 */
public class NodeDisconnectingState extends State {
    private static Logger logger = Logger.getLogger(NodeDisconnectingState.class);

    public Node getNode () {
        return (Node) getContainer();
    }

    public NodeDisconnectingState (Node node) {
        super(node);
    }

    @Override
    public State processMessage(Message message) {
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

    public State processStopResponseWireMessage (StopResponseWireMessage stopResponseWireMessage) {
        logger.info (getNode() + " got stop response.");

        return new NodeStoppingState(getNode());
    }

    public State processStopWireMessage (StopWireMessage stopWireMessage) {
        StopResponseWireMessage response = new StopResponseWireMessage();
        getNode().getNetwork().sendNetworkMessage(getNode().getQueue(), this, getNode().getHandle(), response);

        return this;
    }

    public State discardMessage (Message message) {
        logger.warn (getNode() + " is discarding " + message);

        return this;
    }

    public State discardWireMessage (WireMessage wireMessage) {
        logger.warn (getNode() + " is discarding a network message " + wireMessage);

        StoppingWireMessage stoppingWireMessage = new StoppingWireMessage();
        getNode().getNetwork().sendNetworkMessage(getNode().getQueue(), this, getNode().getHandle(), stoppingWireMessage);

        return this;
    }
}
