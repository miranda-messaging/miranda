package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.SubscriptionHolder;
import com.ltsllc.miranda.subsciptions.messages.*;

/**
 * Created by Clark on 4/22/2017.
 */
public class SubscriptionHolderReadyState extends ServletHolderReadyState {
    public SubscriptionHolder getSubscriptionHolder () {
        return (SubscriptionHolder) getContainer();
    }

    public SubscriptionHolderReadyState (SubscriptionHolder subscriptionHolder) {
        super (subscriptionHolder);
    }

    public State processMessage (Message message) {
        State nextState = getSubscriptionHolder().getCurrentState();

        switch (message.getSubject()) {
            case GetSubscriptionResponse: {
                GetSubscriptionResponseMessage getSubscriptionResponseMessage = (GetSubscriptionResponseMessage) message;
                nextState = processGetSubscriptionResponseMessage (getSubscriptionResponseMessage);
                break;
            }

            case GetSubscriptionsResponse: {
                GetSubscriptionsResponseMessage getSubscriptionsResponseMessage = (GetSubscriptionsResponseMessage)
                        message;

                nextState = processGetSubscriptionsResponseMessage (getSubscriptionsResponseMessage);
                break;
            }

            case CreateSubscriptionResponse: {
                CreateSubscriptionResponseMessage createSubscriptionResponseMessage = (CreateSubscriptionResponseMessage)
                        message;

                nextState = processCreateSubscriptionResponseMessage (createSubscriptionResponseMessage);
                break;
            }

            case UpdateSubscriptionResponse: {
                UpdateSubscriptionResponseMessage updateSubscriptionResponseMessage = (UpdateSubscriptionResponseMessage)
                        message;

                nextState = processUpdateSubscriptionResponseMessage(updateSubscriptionResponseMessage);
                break;
            }

            case DeleteSubscriptionResponse: {
                DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage = (DeleteSubscriptionResponseMessage)
                        message;

                nextState = processDeleteSubscriptionResponseMessage(deleteSubscriptionResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGetSubscriptionResponseMessage (GetSubscriptionResponseMessage getSubscriptionResponseMessage) {
        getSubscriptionHolder().setSubscriptionAndAwaken(getSubscriptionResponseMessage.getResult(),
                getSubscriptionResponseMessage.getSubscription());

        return getSubscriptionHolder().getCurrentState();
    }

    public State processGetSubscriptionsResponseMessage (GetSubscriptionsResponseMessage getSubscriptionsResponseMessage) {
        getSubscriptionHolder().setSubscriptionsAndAwaken(getSubscriptionsResponseMessage.getSubscriptions());

        return getSubscriptionHolder().getCurrentState();
    }

    public State processCreateSubscriptionResponseMessage (CreateSubscriptionResponseMessage createSubscriptionResponseMessage) {
        getSubscriptionHolder().setCreateResultAndAwaken(createSubscriptionResponseMessage.getResult());

        return getSubscriptionHolder().getCurrentState();
    }

    public State processUpdateSubscriptionResponseMessage (UpdateSubscriptionResponseMessage updateSubscriptionResponseMessage) {
        getSubscriptionHolder().setUpdateResultAndAwaken(updateSubscriptionResponseMessage.getResult());

        return getSubscriptionHolder().getCurrentState();
    }

    public State processDeleteSubscriptionResponseMessage (DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage) {
        getSubscriptionHolder().setDeleteResultAndAwaken(deleteSubscriptionResponseMessage.getResult());

        return getSubscriptionHolder().getCurrentState();
    }
}
