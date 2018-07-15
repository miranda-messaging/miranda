package com.ltsllc.miranda.eventqueue.states;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.eventqueue.EventQueueManager;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.states.DirectoryManagerLoadingState;
import com.ltsllc.miranda.manager.states.ManagerLoadingState;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.messages.ScanResponseMessage;
import com.ltsllc.miranda.subsciptions.messages.GetSubscriptionResponseMessage;
import org.apache.log4j.Logger;

public class EventQueueManagerLoadingState extends DirectoryManagerLoadingState {
    private int numberOfSubscriptionsWaitingOn = 0;

    private static Logger LOGGER = Logger.getLogger(EventQueueManagerLoadingState.class);

    public EventQueueManager getEventQueueManager () {
        return (EventQueueManager) getContainer();
    }

    public EventQueueManagerLoadingState(EventQueueManager eventQueueManager) throws MirandaException {
        super(eventQueueManager, new EventQueueManagerReadyState(eventQueueManager));
    }

    public int getNumberOfSubscriptionsWaitingOn() {
        return numberOfSubscriptionsWaitingOn;
    }

    public void setNumberOfSubscriptionsWaitingOn(int numberOfSubscriptionsWaitingOn) {
        this.numberOfSubscriptionsWaitingOn = numberOfSubscriptionsWaitingOn;
    }

    public void decrementNumberOfSubscriptionsWaitingOn () {
        this.numberOfSubscriptionsWaitingOn--;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getEventQueueManager().getCurrentState();

        switch (message.getSubject()) {
            case GetSubscriptionResponse: {
                GetSubscriptionResponseMessage getSubscriptionResponseMessage =
                        (GetSubscriptionResponseMessage) message;
                nextState = processGetSubscriptionResponseMessage(getSubscriptionResponseMessage);
                break;
            }

            case ScanResponseMessage: {
                ScanResponseMessage scanResponseMessage = (ScanResponseMessage) message;
                nextState = processScanResponseMessage(scanResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processScanResponseMessage (ScanResponseMessage scanResponseMessage) {
        super.processScanResponseMessage(scanResponseMessage);

        int count = 0;
        for (EventQueue eventQueue : getEventQueueManager().getEventQueues()) {
            Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionMessage(getEventQueueManager().getQueue(),
                    getEventQueueManager(), eventQueue.getSubscriptionName());
            count++;
        }

        setNumberOfSubscriptionsWaitingOn(count);
        return this;
    }

    public State processGetSubscriptionResponseMessage (GetSubscriptionResponseMessage getSubscriptionResponseMessage) {
        if (getSubscriptionResponseMessage.getResult() != Results.Success)
        {
            LOGGER.warn ("Could not find Subscription for EventQueue.  Looking for");
            return getEventQueueManager().getCurrentState();
        }

        EventQueue eventQueue = getEventQueueManager().getEventQueueFor(getSubscriptionResponseMessage.getSubscription().getName());
        if (eventQueue == null) {
            LOGGER.warn ("Could not find EventQueue for Subscription.  Looking for " + getSubscriptionResponseMessage.getSubscription().getName());
            return getEventQueueManager().getCurrentState();
        }

        getSubscriptionResponseMessage.getSubscription().setEventQueue(eventQueue);
        decrementNumberOfSubscriptionsWaitingOn();

        if (getNumberOfSubscriptionsWaitingOn() < 1)
            return getReadyState();
        else
            return getEventQueueManager().getCurrentState();
    }
}
