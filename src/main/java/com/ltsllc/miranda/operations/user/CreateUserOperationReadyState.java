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
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.user.messages.CreateUserResponseMessage;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateUserOperationReadyState extends State {
    public CreateUserOperation getCreateUserOperation () {
        return (CreateUserOperation) getContainer();
    }

    public CreateUserOperationReadyState (CreateUserOperation createUserOperation) {
        super(createUserOperation);
    }

    public State processMessage (Message message) {
        State nextState = getCreateUserOperation().getCurrentState();

        switch (message.getSubject()) {
            case CreateUserResponse: {
                CreateUserResponseMessage createUserResponseMessage = (CreateUserResponseMessage) message;
                nextState = processCreateUserResponseMessage(createUserResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateUserResponseMessage (CreateUserResponseMessage message) {
        if (message.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendNewUserMessage(getCreateUserOperation().getQueue(),
                    this, message.getUser());
        }

        CreateUserResponseMessage createUserResponseMessage = new CreateUserResponseMessage(
                getCreateUserOperation().getQueue(), this, message.getUser(), message.getResult());

        send(getCreateUserOperation().getRequester(), createUserResponseMessage);

        return StopState.getInstance();
    }
}
