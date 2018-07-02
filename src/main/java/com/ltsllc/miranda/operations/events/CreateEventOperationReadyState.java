package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.event.messages.CreateEventMessage;
import com.ltsllc.miranda.event.messages.CreateEventResponseMessage;
import com.ltsllc.miranda.message.Message;

public class CreateEventOperationReadyState extends State {
    public CreateEventOperationReadyState (CreateEventOperation createEventOperation) {
        super(createEventOperation);
    }

    public CreateEventOperation getCreateEventOperation () {
        return (CreateEventOperation) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getCreateEventOperation().getCurrentState();

        switch (message.getSubject()) {
            case CreateEventResponse : {
                CreateEventResponseMessage createEventResponseMessage = (CreateEventResponseMessage) message;
                nextState = processCreateEventResponseMessage(createEventResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateEventResponseMessage(CreateEventResponseMessage createEventResponseMessage) {
        if (createEventResponseMessage.getResult() == Results.Success) {
            CreateEventResponseMessage response = new CreateEventResponseMessage(createEventResponseMessage);
            response.setSender(getCreateEventOperation().getQueue());
            response.setWhere(new Exception());
            getCreateEventOperation().respondToRequester(response);
        }

        return getCreateEventOperation().getCurrentState();
    }
}
