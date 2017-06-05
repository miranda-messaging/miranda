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

package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.session.LoginResponseMessage;
import com.ltsllc.miranda.session.messages.CreateSessionResponseMessage;
import com.ltsllc.miranda.session.messages.GetSessionResponseMessage;

/**
 * Created by Clark on 4/16/2017.
 */
public class LoginOperationReadyState extends State {
    public LoginOperation getLoginOperation() {
        return (LoginOperation) getContainer();
    }

    public LoginOperationReadyState(LoginOperation loginOperation) {
        super(loginOperation);
    }

    public State processMessage(Message message) {
        State nextState = getLoginOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetSessionResponse: {
                GetSessionResponseMessage getSessionResponseMessage = (GetSessionResponseMessage) message;
                nextState = processGetSessionResponseMessage(getSessionResponseMessage);
                break;
            }

            case CreateSessionResponse: {
                CreateSessionResponseMessage createSessionResponseMessage = (CreateSessionResponseMessage) message;
                nextState = processCreateSessionResponseMessage(createSessionResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
            }
        }

        return nextState;
    }

    public State processGetSessionResponseMessage(GetSessionResponseMessage getSessionResponseMessage) {
        if (getSessionResponseMessage.getResult() == Results.SessionCreated) {
            Miranda.getInstance().getCluster().sendNewSession(getLoginOperation().getQueue(), this,
                    getSessionResponseMessage.getSession());
        }


        LoginResponseMessage loginResponseMessage = new LoginResponseMessage(getLoginOperation().getQueue(),
                this, getSessionResponseMessage.getResult(), getSessionResponseMessage.getSession());

        send(getLoginOperation().getRequester(), loginResponseMessage);

        return StopState.getInstance();
    }

    public State processCreateSessionResponseMessage(CreateSessionResponseMessage createSessionResponseMessage) {
        Results loginResult;

        if (createSessionResponseMessage.getResult() == Results.Success)
            loginResult = Results.SessionCreated;
        else
            loginResult = createSessionResponseMessage.getResult();

        LoginResponseMessage resultMessage = new LoginResponseMessage(getLoginOperation().getQueue(),
                this, loginResult, createSessionResponseMessage.getSession());

        send(getLoginOperation().getRequester(), resultMessage);

        return StopState.getInstance();
    }

}
