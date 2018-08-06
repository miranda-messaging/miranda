package com.ltsllc.miranda.event.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.subsciptions.messages.GetSubscriptionResponseMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import com.ltsllc.miranda.writer.WriteResponseMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.io.File;

public class EventQueueReadyState extends State {
    private Logger LOGGER = Logger.getLogger(EventQueueReadyState.class);

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

            case GetSubscriptionResponse: {
                GetSubscriptionResponseMessage getSubscriptionResponseMessage = (GetSubscriptionResponseMessage) message;
                nextState = processGetSubscriptionResponseMessage (getSubscriptionResponseMessage);
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

        Miranda.getInstance().getDeliveryManager().sendDeliverEvent(newEventMessage.getEvent(), getEventQueue().getSubscription(),
                getEventQueue().getQueue(), getEventQueue());

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

    public State processGetSubscriptionResponseMessage (GetSubscriptionResponseMessage getSubscriptionResponseMessage) {
        if (getSubscriptionResponseMessage.getResult() == Results.Success) {
            getEventQueue().setSubscription(getSubscriptionResponseMessage.getSubscription());
            getSubscriptionResponseMessage.getSubscription().setEventQueue(getEventQueue());
        } else {
            LOGGER.error("could not find a subscription named " + getEventQueue().getSubscriptionName());
        }

        return getEventQueue().getCurrentState();
    }
}
