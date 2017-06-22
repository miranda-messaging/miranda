/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.StandardManagerReadyState;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.subsciptions.messages.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/12/2017.
 */
public class SubscriptionManagerReadyState extends StandardManagerReadyState<Subscription> {
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

            case ListSubscriptions: {
                LIstSubscriptionsMessage getSubscriptionsMessage = (LIstSubscriptionsMessage) message;
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

    public State processGetSubscriptionsMessage (LIstSubscriptionsMessage getSubscriptionsMessage) {
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
        getSubscriptionManager().deleteSubscription(deleteSubscriptionMessage.getSubscriptionName());

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
