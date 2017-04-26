package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.subsciptions.messages.*;

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

            case CreateSubscription: {
                CreateSubscriptionMessage createSubscriptionMessage = (CreateSubscriptionMessage) message;
                nextState = processCreateSubscriptionMessage (createSubscriptionMessage);
                break;
            }

            case GetSubcription: {
                GetSubscriptionMessage getSubscriptionMessage = (GetSubscriptionMessage) message;
                nextState = processGetSubscriptionMessage (getSubscriptionMessage);
                break;
            }

            case GetSubscriptions: {
                GetSubscriptionsMessage getSubscriptionsMessage = (GetSubscriptionsMessage) message;
                nextState = processGetSubscriptionsMessage (getSubscriptionsMessage);
                break;
            }

            case UpdateSubscription: {
                UpdateSubscriptionMessage updateSubscriptionMessage = (UpdateSubscriptionMessage) message;
                nextState = processUpdateSubscriptionMessage (updateSubscriptionMessage);
                break;
            }

            case DeleteSubscription: {
                DeleteSubscriptionMessage deleteSubscriptionMessage = (DeleteSubscriptionMessage) message;
                nextState = processDeleteSubscriptionMessage (deleteSubscriptionMessage);
                break;
            }

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage (fileLoadedMessage);
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

    public State processCreateSubscriptionMessage(CreateSubscriptionMessage createSubscriptionMessage) {
        Results result = getSubscriptionManager().createSubscription (createSubscriptionMessage.getSubscription());
        CreateSubscriptionResponseMessage response = new CreateSubscriptionResponseMessage(getSubscriptionManager().getQueue(),
                this, result);
        createSubscriptionMessage.reply(response);

        return getSubscriptionManager().getCurrentState();
    }

    public State processGetSubscriptionMessage (GetSubscriptionMessage getSubscriptionMessage) {
        Results result;

        Subscription subscription = getSubscriptionManager().findSubscription(getSubscriptionMessage.getName());
        if (subscription == null)
            result = Results.UserNotFound;
        else
            result = Results.Success;

        GetSubscriptionResponseMessage response = new GetSubscriptionResponseMessage(getSubscriptionManager().getQueue(),
                this, result, subscription);

        getSubscriptionMessage.reply(response);

        return getSubscriptionManager().getCurrentState();
    }

    public State processGetSubscriptionsMessage (GetSubscriptionsMessage getSubscriptionsMessage) {
        GetSubscriptionsResponseMessage response = new GetSubscriptionsResponseMessage(getSubscriptionManager().getQueue(),
                this, getSubscriptionManager().getSubscriptions());

        getSubscriptionsMessage.reply(response);

        return getSubscriptionManager().getCurrentState();
    }

    public State processUpdateSubscriptionMessage (UpdateSubscriptionMessage updateSubscriptionMessage) {
        getSubscriptionManager().updateSubscription(updateSubscriptionMessage.getSubscription());

        UpdateSubscriptionResponseMessage response = new UpdateSubscriptionResponseMessage(getSubscriptionManager().getQueue(),
                this, Results.Success);

        updateSubscriptionMessage.reply(response);

        return getSubscriptionManager().getCurrentState();
    }

    public State processDeleteSubscriptionMessage (DeleteSubscriptionMessage deleteSubscriptionMessage) {
        getSubscriptionManager().deleteSubscription(deleteSubscriptionMessage.getName());

        DeleteSubscriptionResponseMessage response = new DeleteSubscriptionResponseMessage(getSubscriptionManager().getQueue(),
                this, Results.Success);

        deleteSubscriptionMessage.reply(response);

        return getSubscriptionManager().getCurrentState();
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) {
        List<Subscription> subscriptions = (List<Subscription>) fileLoadedMessage.getData();

        getSubscriptionManager().setSubscriptions (subscriptions);

        return getSubscriptionManager().getCurrentState();
    }
}
