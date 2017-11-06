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

package com.ltsllc.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryResponseMessage;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.user.messages.DeleteUserResponseMessage;

/**
 * Created by Clark on 4/16/2017.
 */
public class DeleteUserOperationReadyState extends State {
    public DeleteUserOperation getDeleteUserOperation () {
        return (DeleteUserOperation) getContainer();
    }

    public DeleteUserOperationReadyState (DeleteUserOperation deleteUserOperation) throws MirandaException {
        super(deleteUserOperation);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getDeleteUserOperation().getCurrentState();

        switch (message.getSubject()) {
            case OwnerQueryResponse: {
                OwnerQueryResponseMessage ownerQueryResponseMessage = (OwnerQueryResponseMessage) message;
                nextState = processOwnerQueryResponseMessage (ownerQueryResponseMessage);
                break;
            }

            case DeleteUserResponse: {
                DeleteUserResponseMessage deleteUserResponseMessage = (DeleteUserResponseMessage) message;
                nextState = processDeleteUserResponseMessage(deleteUserResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processOwnerQueryResponseMessage (OwnerQueryResponseMessage message) {
        if (message.getProperty().size() > 0) {
            Results result = Results.Unknown;
            if (message.getSendingManager().equalsIgnoreCase(TopicManager.NAME))
                result = Results.UserOwnsTopics;
            else if (message.getSendingManager().equalsIgnoreCase(SubscriptionManager.NAME))
                result = Results.UserOwnsSubscriptions;

            DeleteUserResponseMessage deleteUserResponseMessage = new DeleteUserResponseMessage(getDeleteUserOperation().getQueue(),
                    this, getDeleteUserOperation().getName());
            deleteUserResponseMessage.setResult(Results.UserOwnsProperty);

            send (getDeleteUserOperation().getRequester(), deleteUserResponseMessage);
            return StopState.getInstance();
        } else {
            getDeleteUserOperation().subsystemResponded(message.getSendingManager());
        }

        if (getDeleteUserOperation().getSubsystems().size() < 1) {
            Miranda.getInstance().getUserManager().sendDeleteUserMessage(getDeleteUserOperation().getQueue(),
                    this, getDeleteUserOperation().getUser());
        }

        return getDeleteUserOperation().getCurrentState();
    }

    public State processDeleteUserResponseMessage (DeleteUserResponseMessage message) {
        if (message.getResult() == Results.Success) {
            DeleteUserResponseMessage deleteUserResponseMessage = new DeleteUserResponseMessage(
                    getDeleteUserOperation().getQueue(), this, getDeleteUserOperation().getName());

            deleteUserResponseMessage.setResult(Results.Success);

            send (getDeleteUserOperation().getRequester(), deleteUserResponseMessage);

            Miranda.getInstance().getCluster().sendDeleteUserMessage(
                    getDeleteUserOperation().getQueue(), this, getDeleteUserOperation().getSession(),
                    getDeleteUserOperation().getUser());
        } else {
            DeleteUserResponseMessage deleteUserResponseMessage = new DeleteUserResponseMessage(
                    getDeleteUserOperation().getQueue(), this, getDeleteUserOperation().getName());

            deleteUserResponseMessage.setResult(message.getResult());
            send(getDeleteUserOperation().getRequester(), deleteUserResponseMessage);
        }

        return StopState.getInstance();
    }

}
