package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/12/2017.
 */
public class SubscriptionManagerReadyState extends State {
    public SubscriptionManager getSubscriptionManager () {
        return (SubscriptionManager) getContainer();
    }

    public SubscriptionManagerReadyState(SubscriptionManager subscriptionManager) {
        super(subscriptionManager);
    }

    public State processMessage (Message message) {
        State nextState = getSubscriptionManager().getCurrentState();

        switch (message.getSubject()) {
            case OwnerQuery: {
                OwnerQueryMessage ownerQueryMessage = (OwnerQueryMessage) message;
                nextState = processOwnerQueryMessage (ownerQueryMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }

    public State processOwnerQueryMessage (OwnerQueryMessage ownerQueryMessage) {
        List<String> property = new ArrayList<String>();

        OwnerQueryResponseMessage ownerQueryResponseMessage = new OwnerQueryResponseMessage(ownerQueryMessage.getSender(),
                this, ownerQueryMessage.getName(), property, SubscriptionManager.NAME);

        ownerQueryMessage.reply(ownerQueryResponseMessage);

        return getSubscriptionManager().getCurrentState();
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getSubscriptionManager().performGarbageCollection();

        return getSubscriptionManager().getCurrentState();
    }
}
