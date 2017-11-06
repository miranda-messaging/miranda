package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.event.messages.NewEventResponseMessage;
import com.ltsllc.miranda.Quorum;

/**
 * We are waiting on the other nodes to acknowledge or record the Event.
 */
public class NewEventOperationAwaitingQuorum extends AwaitingQuorumState {
    public NewEventOperationAwaitingQuorum (NewEventOperation newEventOperation, Quorum quorum) throws MirandaException {
        super(newEventOperation, quorum);
    }

    public NewEventOperation getNewEventOperation () {
        return (NewEventOperation) getContainer();
    }

    public Message createResponseMessage (Results result) {
        NewEventResponseMessage newEventResponseMessage = new NewEventResponseMessage(getNewEventOperation().getQueue(),
                this, result);

        if (result == Results.Success) {
            newEventResponseMessage.setEvent(getNewEventOperation().getEvent());
        }

        return newEventResponseMessage;
    }
}
