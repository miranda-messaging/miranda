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

package com.ltsllc.miranda.session.operations;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.session.messages.CreateSessionResponseMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

/**
 * Created by Clark on 4/18/2017.
 */
public class CreateSessionOperationReadyState extends State {
    public CreateSessionOperation getCreateSessionOperation () {
        return (CreateSessionOperation) getContainer();
    }

    public CreateSessionOperationReadyState (CreateSessionOperation createSessionOperation) {
        super(createSessionOperation);
    }

    public State processMessage (Message message) {
        State nextState = getCreateSessionOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage(getUserResponseMessage);
                break;
            }

            case CreateSessionResponse: {
                CreateSessionResponseMessage createSessionResponseMessage = (CreateSessionResponseMessage) message;
                nextState = processCreateSessionResponseMessage(createSessionResponseMessage);
                break;
            }
        }
        return nextState;
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() == Results.Success) {
            Miranda.getInstance().getSessionManager().sendCreateSession(getCreateSessionOperation().getQueue(),
                    this, getUserResponseMessage.getUser());

            return getCreateSessionOperation().getCurrentState();
        } else {
            CreateSessionResponseMessage response = new CreateSessionResponseMessage(getCreateSessionOperation().getQueue(),
                    this, Results.UserNotFound, null);
            send (getCreateSessionOperation().getRequester(), response);

            return StopState.getInstance();
        }
    }

    public State processCreateSessionResponseMessage (CreateSessionResponseMessage createSessionResponseMessage) {
        CreateSessionResponseMessage response = new CreateSessionResponseMessage(getCreateSessionOperation().getQueue(),
                this, createSessionResponseMessage.getResult(), createSessionResponseMessage.getSession());

        send (getCreateSessionOperation().getRequester(), response);

        return StopState.getInstance();
    }
}
