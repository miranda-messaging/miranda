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

package com.ltsllc.miranda.operations.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionOperation extends Operation {
    public static final String NAME = "delete subscription operation";

    private String subscriptionName;
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public DeleteSubscriptionOperation(BlockingQueue<Message> requester, Session session, String subscriptionName) throws MirandaException {
        super(NAME, requester, session);

        this.subscriptionName = subscriptionName;

        DeleteSubscriptionOperationReadyState readyState = new DeleteSubscriptionOperationReadyState(this);
        setCurrentState(readyState);
    }

    public void start() {
        super.start();

        Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionMessage(getQueue(), this, getSubscriptionName());
    }
}
