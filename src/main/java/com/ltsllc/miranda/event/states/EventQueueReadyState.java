package com.ltsllc.miranda.event.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.deliveries.DeliveryAttempt;
import com.ltsllc.miranda.deliveries.messages.DeliveryResultMessage;
import com.ltsllc.miranda.event.messages.AttemptDeliveryMessage;
import com.ltsllc.miranda.event.messages.GetEventReplyMessage;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.subsciptions.messages.GetSubscriptionResponseMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import com.ltsllc.miranda.writer.WriteResponseMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.io.File;

public class EventQueueReadyState extends State {
    private Logger LOGGER = Logger.getLogger(EventQueueReadyState.class);

    public EventQueue getEventQueue() {
        return (EventQueue) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getEventQueue().getCurrentState();

        switch (message.getSubject()) {
            case AttemptDelivery: {
                AttemptDeliveryMessage attemptDeliveryMessage = (AttemptDeliveryMessage) message;
                nextState = processAttemptDeliveryMessage(attemptDeliveryMessage);
                break;
            }

            case NewEvent: {
                NewEventMessage newEventMessage = (NewEventMessage) message;
                nextState = processNewEventMessage(newEventMessage);
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
                nextState = processGetSubscriptionResponseMessage(getSubscriptionResponseMessage);
                break;
            }

            case DeliveryResult: {
                DeliveryResultMessage deliveryResultMessage = (DeliveryResultMessage) message;
                nextState = processDeliveryResultMessage(deliveryResultMessage);
                break;
            }

            case GetEventReply: {
                GetEventReplyMessage getEventReplyMessage = (GetEventReplyMessage) message;
                nextState = processGetEventReplyMessage (getEventReplyMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    public State processNewEventMessage(NewEventMessage newEventMessage) {
        getEventQueue().newEvent(newEventMessage.getEvent());

        WriteMessage writeMessage = new WriteMessage(getEventQueue().getQueue(), getEventQueue());
        Miranda.timer.sendScheduleOnce(1000, getEventQueue().getQueue(), writeMessage);

        if (getEventQueue().getEvents().size() < 2) {
            Miranda.getInstance().getDeliveryManager().sendDeliverEvent(newEventMessage.getEvent(), getEventQueue().getSubscription(),
                    getEventQueue().getQueue(), getEventQueue());
        }

        return getEventQueue().getCurrentState();
    }

    public State processWriteMessage(WriteMessage writeMessage) {
        String filename = Miranda.properties.getProperty(MirandaProperties.PROPERTY_EVENT_QUEUE_DIRECTORY) +
                File.separator + getEventQueue().getName();

        Miranda.getInstance().getWriter().sendWrite(getEventQueue().getQueue(), getEventQueue(), filename,
                getEventQueue().getData());

        return getEventQueue().getCurrentState();
    }

    public State processWriteResponseMessage(WriteResponseMessage writeResponseMessage) {
        return getEventQueue().getCurrentState();
    }

    public State processGetSubscriptionResponseMessage(GetSubscriptionResponseMessage getSubscriptionResponseMessage) {
        if (getSubscriptionResponseMessage.getResult() == Results.Success) {
            getEventQueue().setSubscription(getSubscriptionResponseMessage.getSubscription());
            getSubscriptionResponseMessage.getSubscription().setEventQueue(getEventQueue());
        } else {
            LOGGER.error("could not find a subscription named " + getEventQueue().getSubscriptionName());
        }

        return getEventQueue().getCurrentState();
    }


    public State processDeliveryResultMessage(DeliveryResultMessage deliveryResultMessage) {
        if (deliveryResultMessage.getResult() == Results.Success) {
            if (getEventQueue().getEvents().size() > 1) {
                String eventId = getEventQueue().getEvents().get(1);
                Miranda.getInstance().getEventManager().sendGetEvent(eventId, getEventQueue().getQueue(), getEventQueue());
            }
        } else {
            getEventQueue().incrementNumberOfTries(deliveryResultMessage.getEvent());
            long nextTry = getEventQueue().getTimeOfNextTry(deliveryResultMessage.getEvent());
            if (nextTry > 0) {
                long now = System.currentTimeMillis();
                long timeTillTry = nextTry - now;
                AttemptDeliveryMessage attemptDeliverMessage = new AttemptDeliveryMessage();
                Miranda.timer.sendScheduleOnce(timeTillTry, getEventQueue().getQueue(), attemptDeliverMessage);
            }

        }
        return getEventQueue().getCurrentState();
    }

    public State processAttemptDeliveryMessage (AttemptDeliveryMessage attemptDeliveryMessage) {
        if (null != getEventQueue().getCurrentEvent()) {
            Event event = getEventQueue().getCurrentEvent();
            Miranda.getInstance().getDeliveryManager().sendDeliverEvent(event, getEventQueue().getSubscription(),
                    getEventQueue().getQueue(), getEventQueue());
        }

        return getEventQueue().getCurrentState();
    }

    public State processGetEventReplyMessage(GetEventReplyMessage getEventReplyMessage) {
        if (getEventReplyMessage.getResult() != Results.Success) {
            Panic panic = new Panic("could not find event for id " + getEventReplyMessage.getId());
            Miranda.panicMiranda(panic);
        }
        else {
            getEventQueue().setCurrentEvent(getEventReplyMessage.getEvent());
            Miranda.getInstance().getDeliveryManager().sendDeliverEvent(getEventQueue().getCurrentEvent(),
                    getEventQueue().getSubscription(), getEventQueue().getQueue(), getEventQueue());
        }

        return getEventQueue().getCurrentState();
    }
}

