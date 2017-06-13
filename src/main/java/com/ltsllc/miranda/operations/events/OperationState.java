package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.util.ImprovedRandom;

/**
 * Created by Clark on 6/11/2017.
 */
abstract public class OperationState extends State {
    abstract public Message createResponseMessage (Results result);

    private ImprovedRandom improvedRandom;

    public OperationState (Operation operation) {
        super(operation);

        this.improvedRandom = new ImprovedRandom();
    }

    public Operation getOperation () {
        return (Operation) getContainer();
    }

    public ImprovedRandom getImprovedRandom() {
        return improvedRandom;
    }

    public void reply (Results result) {
        Message response = createResponseMessage(result);
        Consumer.staticSend(response, getOperation().getRequester());
    }

    public String createConversationKey () {
        Long value = getImprovedRandom().nextNonNegativeLong();
        return value.toString();
    }
}
