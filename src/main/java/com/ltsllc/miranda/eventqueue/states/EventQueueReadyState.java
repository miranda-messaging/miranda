package com.ltsllc.miranda.eventqueue.states;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.writer.WriteResponseMessage;

/**
 * The default state for an event queue --- receive new events
 */
public class EventQueueReadyState extends State {
    public EventQueueReadyState (EventQueue eventQueue) {
        setContainer(eventQueue);
    }

    public EventQueue getEventQueue () {
        return (EventQueue) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getEventQueue().getCurrentState();

        switch (message.getSubject()) {
            case NewEvent: {
                NewEventMessage newEventMessage = (NewEventMessage) message;
                nextState = processNewEventMessage(newEventMessage);
                break;
            }

            case WriteResponse: {
                WriteResponseMessage writeResponseMessage = (WriteResponseMessage) message;
                nextState = processWriteResponseMessage(writeResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    public State processNewEventMessage (NewEventMessage newEventMessage) {
        getEventQueue().newEvent (newEventMessage.getEvent());

        return getEventQueue().getCurrentState();
    }

    public State processWriteResponseMessage (WriteResponseMessage writeResponseMessage) {
        if (writeResponseMessage.getResult() != Results.Success) {
            Panic panic = new Panic("Writing an event queue was unsuccessful", Panic.Reasons.EventQueueWriteFailed);
            Miranda.panicMiranda(panic);
        }

        return getEventQueue().getCurrentState();
    }
}
