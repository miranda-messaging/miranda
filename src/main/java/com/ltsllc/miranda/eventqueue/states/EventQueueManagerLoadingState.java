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
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.reader.messages.ScanResponseMessage;
import com.ltsllc.miranda.subsciptions.messages.GetSubscriptionResponseMessage;
import org.apache.log4j.Logger;

import java.io.File;

public class EventQueueManagerLoadingState extends DirectoryManagerLoadingState {
    private int numberOfSubscriptionsWaitingOn = 0;

    private static Logger LOGGER = Logger.getLogger(EventQueueManagerLoadingState.class);

    public EventQueueManager getEventQueueManager () {
        return (EventQueueManager) getContainer();
    }

    public EventQueueManagerLoadingState(EventQueueManager eventQueueManager) throws MirandaException {
        super(eventQueueManager);
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
            case ScanResponse: {
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
        try {
            super.processScanResponseMessage(scanResponseMessage);

            for (String entry : scanResponseMessage.getContents()) {
                String filename = Miranda.properties.getProperty(MirandaProperties.PROPERTY_EVENT_QUEUE_DIRECTORY) +
                        File.separator + entry;

                getEventQueueManager().processEntry(filename);
            }

            return new EventQueueManagerReadyState(getEventQueueManager());
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception trying to rectify EventQueues", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return getEventQueueManager().getCurrentState();
        }
    }


 }
