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
        CreateEventMessage createEventMessage = new CreateEventMessage(getCreateEventOperation().getQueue(),
                getCreateEventOperation(), getCreateEventOperation().getEvent(), getCreateEventOperation().getSession());

        send(Miranda.getInstance().getEventManager().getQueue(), createEventMessage);

        getCreateEventOperation().getEventManager().sendCreateEventMessage(getCreateEventOperation().getQueue(),
                getCreateEventOperation(), getCreateEventOperation().getEvent(), getCreateEventOperation().getSession());

        CreateEventOperationReadyState createEventOperationReadyState =
                new CreateEventOperationReadyState(getCreateEventOperation());

        return createEventOperationReadyState;
    }
}
