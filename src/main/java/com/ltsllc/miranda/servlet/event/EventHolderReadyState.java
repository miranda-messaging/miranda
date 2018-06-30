package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.event.messages.CreateEventResponseMessage;
import com.ltsllc.miranda.event.messages.ReadEventResponseMessage;
import com.ltsllc.miranda.servlet.ServletHolderReadyState;

/**
 * Created by Clark on 6/7/2017.
 */
public class EventHolderReadyState extends ServletHolderReadyState {
    public EventHolderReadyState(EventHolder eventHolder) throws MirandaException {
        super(eventHolder);
    }

    public EventHolder getEventHolder() {
        return (EventHolder) getContainer();
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getEventHolder().getCurrentState();

        switch (message.getSubject()) {
            case ReadResponse: {
                ReadEventResponseMessage readEventResponseMessage = (ReadEventResponseMessage) message;
                nextState = processReadEventResponseMessage(readEventResponseMessage);
                break;
            }

            case CreateResponse: {
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

    public State processReadEventResponseMessage(ReadEventResponseMessage readEventResponseMessage) {
        getEventHolder().setReadResultAndAwaken(readEventResponseMessage.getResult(), readEventResponseMessage.getEvent());

        return getEventHolder().getCurrentState();
    }

    public State processCreateEventResponseMessage(CreateEventResponseMessage createEventResponseMessage) {
        getEventHolder().setCreateResultAndAwaken(createEventResponseMessage.getResult(),
                createEventResponseMessage.getGuid());

        return getEventHolder().getCurrentState();
    }
}
