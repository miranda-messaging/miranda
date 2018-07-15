package com.ltsllc.miranda.event.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.writer.WriteMessage;
import com.ltsllc.miranda.writer.WriteResponseMessage;

import java.io.File;

public class EventQueueReadyState extends State {
    public EventQueueReadyState (Consumer consumer) {
        super(consumer);
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
                nextState = processNewEventMessage (newEventMessage);
                break;
            }

            case Write: {
                WriteMessage writeMessage = (WriteMessage) message;
                nextState = processWriteMessage(writeMessage);
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
        getEventQueue().newEvent(newEventMessage.getEvent());

        WriteMessage writeMessage = new WriteMessage(getEventQueue().getQueue(), getEventQueue());
        Miranda.timer.sendScheduleOnce(1000, getEventQueue().getQueue(), writeMessage);

        return getEventQueue().getCurrentState();
    }

    public State processWriteMessage (WriteMessage writeMessage) {
        String filename = Miranda.properties.getProperty(MirandaProperties.PROPERTY_EVENT_QUEUE_DIRECTORY) +
                File.separator + getEventQueue().getName();

        Miranda.getInstance().getWriter().sendWrite(getEventQueue().getQueue(), getEventQueue(), filename,
                getEventQueue().getData());

        return getEventQueue().getCurrentState();
    }

    public State processWriteResponseMessage (WriteResponseMessage writeResponseMessage) {
        return getEventQueue().getCurrentState();
    }
}
