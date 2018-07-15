package com.ltsllc.miranda.subsciptions.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.states.ManagerLoadingState;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import org.apache.log4j.Logger;

public class SubscriptionManagerLoadingState extends ManagerLoadingState {
    private static Logger LOGGER = Logger.getLogger(SubscriptionManagerLoadingState.class);

    public SubscriptionManagerLoadingState (SubscriptionManager manager) throws MirandaException {
        super(manager, new SubscriptionManagerReadyState(manager));
    }

    @Override
    public State processFileLoadedMessage(FileLoadedMessage fileLoadedMessage) throws MirandaException {
        super.processFileLoadedMessage(fileLoadedMessage);
        return new SubscriptionManagerReadyState(getSubscriptionManager());
    }

    public SubscriptionManager getSubscriptionManager () {
        return (SubscriptionManager) getContainer();
    }

    @Override
    public void exit() {
        int count = 0;
        // Start all the subscriptions
        LOGGER.debug ("Starting subscriptions");
        for (Subscription subscription : getSubscriptionManager().getSubscriptions()) {
            subscription.start();
            count++;
        }
        LOGGER.debug("Started " + count + " Subscriptions");
    }
}
