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

package com.ltsllc.miranda.servlet.user.state;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.servlet.ServletHolderReadyState;
import com.ltsllc.miranda.servlet.user.UserHolder;
import com.ltsllc.miranda.user.messages.*;

/**
 * Created by Clark on 4/5/2017.
 */
public class UserHolderReadyState extends ServletHolderReadyState {
    public UserHolder getUserHolder() {
        return (UserHolder) getContainer();
    }

    public UserHolderReadyState(UserHolder userHolder) throws MirandaException {
        super(userHolder);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getUserHolder().getCurrentState();

        switch (message.getSubject()) {
            case GetUsersResponse: {
                GetUsersResponseMessage response = (GetUsersResponseMessage) message;
                nextState = processGetUsersResponseMessage(response);
                break;
            }

            case GetUserResponse: {
                GetUserResponseMessage response = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage(response);
                break;
            }

            case CreateUserResponse: {
                CreateUserResponseMessage response = (CreateUserResponseMessage) message;
                nextState = processCreateUserResponseMessage(response);
                break;
            }

            case UpdateUserResponse: {
                UpdateUserResponseMessage response = (UpdateUserResponseMessage) message;
                nextState = processUpdateUserResponseMessage(response);
                break;
            }

            case DeleteUserResponse: {
                DeleteUserResponseMessage response = (DeleteUserResponseMessage) message;
                nextState = processDeleteUserResponseMessage(response);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGetUsersResponseMessage(GetUsersResponseMessage getUsersResponseMessage) {
        getUserHolder().setUsersAndAwaken(getUsersResponseMessage.getUsers());

        return getUserHolder().getCurrentState();
    }

    public State processGetUserResponseMessage(GetUserResponseMessage getUserResponseMessage) {
        getUserHolder().setGetUserResults(getUserResponseMessage.getResult());
        getUserHolder().setUserAndAwaken(getUserResponseMessage.getUser());

        return getUserHolder().getCurrentState();
    }

    public State processCreateUserResponseMessage(CreateUserResponseMessage createUserResponseMessage) {
        getUserHolder().setUserCreatedAndAwaken(createUserResponseMessage.getResult());

        return getUserHolder().getCurrentState();
    }

    public State processUpdateUserResponseMessage(UpdateUserResponseMessage updateUserResponseMessage) {
        getUserHolder().setUserUpdatedAndAwaken(updateUserResponseMessage.getResult());

        return getUserHolder().getCurrentState();
    }

    public State processDeleteUserResponseMessage(DeleteUserResponseMessage deleteUserResponseMessage) {
        getUserHolder().setUserDeletedAndAwaken(deleteUserResponseMessage.getResult());

        return getUserHolder().getCurrentState();
    }
}
