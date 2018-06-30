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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionResponseMessage;
import com.ltsllc.miranda.topics.messages.GetTopicResponseMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

/**
 * Created by Clark on 4/22/2017.
 */
public class CreateSubscriptionOperationReadyState extends State {
    public CreateSubscriptionOperation getCreateSubscriptionOperation() {
        return (CreateSubscriptionOperation) getContainer();
    }

    public CreateSubscriptionOperationReadyState(CreateSubscriptionOperation createSubscriptionOperation) throws MirandaException {
        super(createSubscriptionOperation);
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getCreateSubscriptionOperation().getCurrentState();

        switch (message.getSubject()) {
            case CreateSubscriptionResponse: {
                CreateSubscriptionResponseMessage createSubscriptionResponseMessage = (CreateSubscriptionResponseMessage)
                        message;

                nextState = processCreateSubscriptionResponseMessage(createSubscriptionResponseMessage);
                break;
            }

            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage(getUserResponseMessage);
                break;
            }

            case GetTopicResponse: {
                GetTopicResponseMessage getTopicResponseMessage = (GetTopicResponseMessage) message;
                nextState = processGetTopicResponseMessage(getTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateSubscriptionResponseMessage(CreateSubscriptionResponseMessage createSubscriptionResponseMessage) {
        if (createSubscriptionResponseMessage.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendCreateSubscriptionMessage(getCreateSubscriptionOperation().getQueue(),
                    this, getCreateSubscriptionOperation().getSession(),
                    getCreateSubscriptionOperation().getSubscription());

            Miranda.getInstance().sendAuctionMessage(getCreateSubscriptionOperation().getQueue(), this);
        }

        CreateSubscriptionResponseMessage createSubscriptionResponseMessage2 = new CreateSubscriptionResponseMessage(
                getCreateSubscriptionOperation().getQueue(), this, createSubscriptionResponseMessage.getResult()
        );

        send(getCreateSubscriptionOperation().getRequester(), createSubscriptionResponseMessage2);

        return StopState.getInstance();
    }

    public State processGetUserResponseMessage(GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() != Results.Success) {
            CreateSubscriptionResponseMessage response = new CreateSubscriptionResponseMessage(getCreateSubscriptionOperation().getQueue(),
                    this, Results.UserNotFound);

            send(getCreateSubscriptionOperation().getRequester(), response);

            return StopState.getInstance();
        }

        getCreateSubscriptionOperation().setUserManagerResponded(true);

        if (getCreateSubscriptionOperation().getTopicManagerResponded()) {
            Miranda.getInstance().getSubscriptionManager().sendCreateSubscriptionMessage(getCreateSubscriptionOperation().getQueue(),
                    this, getCreateSubscriptionOperation().getSession(),
                    getCreateSubscriptionOperation().getSubscription());
        }

        return getCreateSubscriptionOperation().getCurrentState();
    }

    public State processGetTopicResponseMessage(GetTopicResponseMessage getTopicResponseMessage) {
        if (getTopicResponseMessage.getTopic() == null) {
            CreateSubscriptionResponseMessage response = new CreateSubscriptionResponseMessage(getCreateSubscriptionOperation().getQueue(),
                    this, Results.TopicNotFound);

            send(getCreateSubscriptionOperation().getRequester(), response);

            return StopState.getInstance();
        }

        getCreateSubscriptionOperation().setTopicManagerResponded(true);

        if (getCreateSubscriptionOperation().getUserManagerResponded()) {
            Miranda.getInstance().getSubscriptionManager().sendCreateSubscriptionMessage(getCreateSubscriptionOperation().getQueue(),
                    this, getCreateSubscriptionOperation().getSession(),
                    getCreateSubscriptionOperation().getSubscription());
        }

        return getCreateSubscriptionOperation().getCurrentState();
    }
}
