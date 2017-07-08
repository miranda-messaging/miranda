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
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;
import com.ltsllc.miranda.user.messages.UpdateUserResponseMessage;

/**
 * Created by Clark on 4/16/2017.
 */
public class UpdateUserOperationReadyState extends State {
    public UpdateUserOperation getUpdateUserOperation () {
        return (UpdateUserOperation) getContainer();
    }

    public UpdateUserOperationReadyState (UpdateUserOperation updateUserOperation) {
        super(updateUserOperation);
    }

    public State processMessage (Message message) {
        State nextState = getUpdateUserOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage (getUserResponseMessage);
                break;
            }

            case UpdateUserResponse: {
                UpdateUserResponseMessage updateUserResponseMessage = (UpdateUserResponseMessage) message;
                nextState = processUpdateUserResponseMessage (updateUserResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processUpdateUserResponseMessage (UpdateUserResponseMessage message) {
        if (message.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendUpdateUserMessage(getUpdateUserOperation().getQueue(), this,
                    message.getUser());
        }

        UpdateUserResponseMessage updateUserResponseMessage = new UpdateUserResponseMessage(
                getUpdateUserOperation().getQueue(), this, message.getUser(), message.getResult());

        send (getUpdateUserOperation().getRequester(), updateUserResponseMessage);

        return StopState.getInstance();
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() != Results.Success) {
            UpdateUserResponseMessage updateUserResponseMessage = new UpdateUserResponseMessage(getUpdateUserOperation().getQueue(),
                    this, null, Results.UserNotFound);

            send (getUpdateUserOperation().getRequester(), updateUserResponseMessage);

            return StopState.getInstance();
        } else if (getUpdateUserOperation().getSession().getUser().getName() != getUserResponseMessage.getUser().getName() &&
                getUpdateUserOperation().getSession().getUser().getCategory() != User.UserTypes.Admin)
        {
            UpdateUserResponseMessage updateUserResponseMessage = new UpdateUserResponseMessage(getUpdateUserOperation().getQueue(),
                    this, null, Results.NotOwner);

            send (getUpdateUserOperation().getRequester(), updateUserResponseMessage);

            return StopState.getInstance();
        } else {
            Miranda.getInstance().getUserManager().sendUpdateUserMessage(getUpdateUserOperation().getQueue(), this,
                    getUpdateUserOperation().getUser());
        }

        return getUpdateUserOperation().getCurrentState();
    }
}
