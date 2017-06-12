package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.operations.Operation;

/**
 * Created by Clark on 6/11/2017.
 */
public abstract class OperationState extends State {
    abstract public Message createResponseMessage (Results result);

    public OperationState (Operation operation) {
        super(operation);
    }

    public Operation getOperation () {
        return (Operation) getContainer();
    }

    public void reply (Results result) {
        Message response = createResponseMessage(result);
        Consumer.staticSend(response, getOperation().getRequester());
    }
}
