package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.event.messages.CreateEventMessage;
import com.ltsllc.miranda.miranda.Miranda;

public class CreateEventOperationStartState extends State {
    public CreateEventOperationStartState(Consumer container) {
        super(container);
    }

    public CreateEventOperation getCreateEventOperation () {
        return (CreateEventOperation) container;
    }

    @Override
    public State start() {
        getCreateEventOperation().getEventManager().sendCreateEventMessage(getCreateEventOperation().getQueue(),
                getCreateEventOperation(), getCreateEventOperation().getEvent(), getCreateEventOperation().getSession());

        CreateEventOperationReadyState createEventOperationReadyState =
                new CreateEventOperationReadyState(getCreateEventOperation());

        return createEventOperationReadyState;
    }
}
