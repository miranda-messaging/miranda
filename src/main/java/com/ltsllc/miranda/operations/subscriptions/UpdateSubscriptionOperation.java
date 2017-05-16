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
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class UpdateSubscriptionOperation extends Operation {
    private Subscription subscription;
    private boolean userManagerResponded;
    private boolean topicManagerResponded;

    public boolean getTopicManagerResponded() {
        return topicManagerResponded;
    }

    public void setTopicManagerResponded(boolean topicManagerResponded) {
        this.topicManagerResponded = topicManagerResponded;
    }

    public boolean getUserManagerResponded() {
        return userManagerResponded;
    }

    public void setUserManagerResponded(boolean userManagerResponded) {
        this.userManagerResponded = userManagerResponded;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public UpdateSubscriptionOperation (BlockingQueue<Message> requester, Session session, Subscription subscription) {
        super("update subscription operations", requester, session);

        this.subscription = subscription;
        setUserManagerResponded(false);
        setTopicManagerResponded(false);

        UpdateSubscriptionOperationReadyState readyState = new UpdateSubscriptionOperationReadyState(this);
        setCurrentState(readyState);
    }


    public void start () {
        super.start();

        Miranda miranda = Miranda.getInstance();
        miranda.getUserManager().sendGetUser(getQueue(), this, getSubscription().getOwner());
        miranda.getTopicManager().sendGetTopicMessage(getQueue(), this, getSubscription().getTopic());
    }
}
