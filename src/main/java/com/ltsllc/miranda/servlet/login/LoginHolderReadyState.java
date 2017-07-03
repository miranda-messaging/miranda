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

package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.Results;
import com.ltsllc.miranda.session.LoginResponseMessage;
import com.ltsllc.miranda.session.messages.GetSessionResponseMessage;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginHolderReadyState extends State {
    public LoginHolder getLoginHolder () {
        return (LoginHolder) getContainer();
    }

    public LoginHolderReadyState (LoginHolder loginHolder) {
        super(loginHolder);
    }

    public State processMessage (Message message) {
        State nextState = getLoginHolder().getCurrentState();

        switch (message.getSubject()) {
            case LoginResponse: {
                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) message;
                nextState = processLoginResponseMessage(loginResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGetSessionResponseMessage (GetSessionResponseMessage getSessionResponseMessage) {
        if (getSessionResponseMessage.getResult() == Results.Success)
                getLoginHolder().setSession(getSessionResponseMessage.getSession());

        LoginHolder.LoginResult loginResult = new LoginHolder.LoginResult(getSessionResponseMessage.getResult(),
                getSessionResponseMessage.getSession());

        getLoginHolder().setResultAndWakeup(loginResult);

        return getLoginHolder().getCurrentState();
    }

    public State processLoginResponseMessage (LoginResponseMessage loginResponseMessage) {
        LoginHolder.LoginResult loginResult = new LoginHolder.LoginResult(loginResponseMessage.getResult(),
                loginResponseMessage.getSession());

        getLoginHolder().setResultAndWakeup(loginResult);

        return getLoginHolder().getCurrentState();
    }
}
