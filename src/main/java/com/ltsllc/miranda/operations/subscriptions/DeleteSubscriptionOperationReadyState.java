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
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionResponseMessage;
import com.ltsllc.miranda.subsciptions.messages.GetSubscriptionResponseMessage;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionOperationReadyState extends State {
    public DeleteSubscriptionOperation getDeleteSubscriptionOperation() {
        return (DeleteSubscriptionOperation) getContainer();
    }

    public DeleteSubscriptionOperationReadyState(DeleteSubscriptionOperation deleteSubscriptionOperation) throws MirandaException {
        super(deleteSubscriptionOperation);
    }

    public State processMessage(Message message) {
        State nextState = getDeleteSubscriptionOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetSubscriptionResponse: {
                GetSubscriptionResponseMessage getSubscriptionResponseMessage = (GetSubscriptionResponseMessage)
                        message;

                nextState = processGetSubscriptionResponseMessage(getSubscriptionResponseMessage);
                break;
            }

            case DeleteSubscriptionResponse: {
                DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage = (DeleteSubscriptionResponseMessage)
                        message;

                nextState = processDeleteSubscriptionResponseMessage(deleteSubscriptionResponseMessage);
                break;
            }
        }

        return nextState;
    }

    public State processDeleteSubscriptionResponseMessage(DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage) {
        if (deleteSubscriptionResponseMessage.getResult() != Results.Success) {
            DeleteSubscriptionResponseMessage response = new DeleteSubscriptionResponseMessage(getDeleteSubscriptionOperation().getQueue(),
                    this, deleteSubscriptionResponseMessage.getResult());

            send(getDeleteSubscriptionOperation().getRequester(), deleteSubscriptionResponseMessage);

            return StopState.getInstance();
        }

        DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage2 = new DeleteSubscriptionResponseMessage(
                getDeleteSubscriptionOperation().getQueue(), this, deleteSubscriptionResponseMessage.getResult());

        send(getDeleteSubscriptionOperation().getRequester(), deleteSubscriptionResponseMessage2);

        return StopState.getInstance();
    }

    public State processGetSubscriptionResponseMessage(GetSubscriptionResponseMessage response) {
        if (response.getResult() != Results.Success) {
            DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage = new DeleteSubscriptionResponseMessage(
                    getDeleteSubscriptionOperation().getQueue(), this, Results.SubscriptionNotFound);

            send(getDeleteSubscriptionOperation().getRequester(), deleteSubscriptionResponseMessage);

            return StopState.getInstance();
        }

        getDeleteSubscriptionOperation().setSubscription(response.getSubscription());

        if (!getDeleteSubscriptionOperation().getSession().getUser().getName().equals(getDeleteSubscriptionOperation().getSubscription().getOwner()) &&
                getDeleteSubscriptionOperation().getSession().getUser().getCategory() != User.UserTypes.Admin) {
            DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage = new DeleteSubscriptionResponseMessage(
                    getDeleteSubscriptionOperation().getQueue(), this, Results.NotOwner);

            return StopState.getInstance();
        }

        Miranda.getInstance().getSubscriptionManager().sendDeleteSubscriptionMessage(getDeleteSubscriptionOperation().getQueue(),
                this, getDeleteSubscriptionOperation().getSession(), getDeleteSubscriptionOperation().getSubscriptionName());

        return getDeleteSubscriptionOperation().getCurrentState();
    }

}
