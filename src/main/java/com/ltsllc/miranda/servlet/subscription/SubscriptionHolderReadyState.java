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

package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.servlet.ServletHolderReadyState;
import com.ltsllc.miranda.subsciptions.messages.*;

/**
 * Created by Clark on 4/22/2017.
 */
public class SubscriptionHolderReadyState extends ServletHolderReadyState {
    public SubscriptionHolder getSubscriptionHolder () {
        return (SubscriptionHolder) getContainer();
    }

    public SubscriptionHolderReadyState (SubscriptionHolder subscriptionHolder) throws MirandaException {
        super (subscriptionHolder);
    }

    public State processMessage (Message message) throws MirandaException {
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
