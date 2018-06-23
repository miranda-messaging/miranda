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

import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.results.GetUserResponseObject;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.session.LoginResponseMessage;
import com.ltsllc.miranda.session.messages.CreateSessionResponseMessage;
import com.ltsllc.miranda.session.messages.GetSessionResponseMessage;
import com.ltsllc.miranda.user.UnknownUserException;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 4/16/2017.
 */
public class LoginOperationReadyState extends State {
    private static Logger LOGGER = Logger.getLogger(LoginOperationReadyState.class);

    public State start() {
        Miranda.getInstance().getUserManager().sendGetUser(getLoginOperation().getQueue(), this, getLoginOperation().getUser());
        return this;
    }

    public LoginOperation getLoginOperation() {
        return (LoginOperation) getContainer();
    }

    public LoginOperationReadyState(LoginOperation loginOperation) throws MirandaException {
        super(loginOperation);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getLoginOperation().getCurrentState();

        try {
            switch (message.getSubject()) {
                case GetUserResponse: {
                    GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                    nextState = processGetUserResponseMessge(getUserResponseMessage);
                    break;
                }

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
        } catch (EncryptionException e) {
            LOGGER.warn("Encountered an EncryptionException, will attempt to continue", e);
        }

        return nextState;
    }

    public State processGetUserResponseMessge(GetUserResponseMessage getUserResponseMessage) throws EncryptionException {
        if (getUserResponseMessage.getResult() == Results.UserNotFound) {
            UnrecognizedUserMessage unrecognizedUserMessage = new UnrecognizedUserMessage(
                    getLoginOperation().getUser(),
                    getLoginOperation().getQueue(),
                    this);
            Consumer.staticSend(unrecognizedUserMessage, getLoginOperation().getRequester());
        } else {
            getLoginOperation().setPublicKey(getUserResponseMessage.getUser().getPublicKey());

            Miranda.getInstance().getSessionManager().sendGetSessionMessage(getLoginOperation().getQueue(),
                    this, getLoginOperation().getUser());

        }

        return this;
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

    public State processCreateSessionResponseMessage (CreateSessionResponseMessage createSessionResponseMessage) {
        if (createSessionResponseMessage.getResult() == Results.SessionCreated) {
            Miranda.getInstance().getCluster().sendNewSession(getLoginOperation().getQueue(), this,
                    createSessionResponseMessage.getSession());
        }

        LoginResponseMessage loginResponseMessage = new LoginResponseMessage(getLoginOperation().getQueue(),
                this, createSessionResponseMessage.getResult(), createSessionResponseMessage.getSession());
        send (getLoginOperation().getRequester(), loginResponseMessage);
        return StopState.getInstance();
    }
}
