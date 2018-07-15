package com.ltsllc.miranda.subsciptions.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.message.Message;

public class SubscriptionReadyState extends State {
    public SubscriptionReadyState (Consumer container) {
        super(container);
    }

    public Subscription getSubscription  () {
        return (Subscription) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getSubscription().getCurrentState();

        switch (message.getSubject()) {
            case NewEvent: {
                NewEventMessage newEventMessage = (NewEventMessage) message;
                nextState = processNewEvent(newEventMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processNewEvent (NewEventMessage newEventMessage) {
        Event event = newEventMessage.getEvent();
        if (getSubscription().getTopic().equals(event.getTopicName())) {
            getSubscription().newEvent(event);
        }

        return getSubscription().getCurrentState();
    }
}
