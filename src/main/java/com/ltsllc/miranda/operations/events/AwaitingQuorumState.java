package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.operations.NetworkConversationMessage;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.Quorum;
import com.ltsllc.miranda.operations.WireResponse;
import org.apache.log4j.Logger;

/**
 * Created by clarkhobbie on 6/12/17.
 */
abstract public class AwaitingQuorumState extends OperationState {
    public static final String NAME = "awaiting quorum state";

    private static Logger logger = Logger.getLogger(AwaitingQuorumState.class);

    private Quorum quorum;

    public AwaitingQuorumState(Operation operation, Quorum quorum) {
        super(operation);

        this.quorum = quorum;
    }

    public Quorum getQuorum() {
        return quorum;
    }

    public Operation getOperation () {
        return (Operation) getContainer();
    }

    public State processMessage (Message message) {
        State nextState = getOperation().getCurrentState();

        switch (message.getSubject()) {
            case NetworkConversationMessage: {
                NetworkConversationMessage networkConversationMessage = (NetworkConversationMessage) message;
                nextState = processNetworkMessage(networkConversationMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public String toString () {
        return NAME;
    }

    public State processNetworkMessage (NetworkConversationMessage message) {
        if (!(message.getWireMessage() instanceof WireResponse)) {
            logger.warn("Got non-response network message: " + message + ", ignoring.");
            return getOperation().getCurrentState();
        }

        WireResponse wireResponse = (WireResponse) message.getWireMessage();
        getQuorum().addResponse(message.getNode(), wireResponse);

        if (getQuorum().complete()) {
            Message responseMessage = createResponseMessage(Results.Success);
            Consumer.staticSend(responseMessage, getOperation().getRequester());

            return StopState.getInstance();
        }

        return getOperation().getCurrentState();
    }
}
