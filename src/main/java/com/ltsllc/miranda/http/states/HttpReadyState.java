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

package com.ltsllc.miranda.http.states;

import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.http.messages.AddServletMessage;
import com.ltsllc.miranda.http.messages.AddServletResponseMessage;
import com.ltsllc.miranda.http.messages.SetupServletsMessage;
import com.ltsllc.miranda.http.messages.StartHttpServerMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * Created by Clark on 3/9/2017.
 */
public class HttpReadyState extends State {
    public HttpReadyState(HttpServer httpServer) throws MirandaException {
        super(httpServer);
    }

    public HttpServer getHttpServer() {
        return (HttpServer) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case SetupServlets: {
                SetupServletsMessage setupServletsMessage = (SetupServletsMessage) message;
                nextState = processSetupServletsMessage(setupServletsMessage);
                break;
            }

            case StartHttpServer: {
                StartHttpServerMessage startHttpServerMessage = (StartHttpServerMessage) message;
                nextState = processStartHttpServerMessage(startHttpServerMessage);
                break;
            }

            case AddServlet: {
                AddServletMessage addServletMessage = (AddServletMessage) message;
                nextState = processAddServletMessage(addServletMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processAddServletMessage(AddServletMessage addServletMessage) throws MirandaException {
        getHttpServer().addServlet(addServletMessage.getServletMapping());
        AddServletResponseMessage addServletResponseMessage = new AddServletResponseMessage(getHttpServer().getQueue(),
                getHttpServer(), addServletMessage.getServletMapping().getPath());
        addServletMessage.reply(addServletResponseMessage);
        return getHttpServer().getCurrentState();
    }

    private State processSetupServletsMessage(SetupServletsMessage message) {
        getHttpServer().addServlets(message.getMappings());

        return this;
    }


    private State processStartHttpServerMessage(StartHttpServerMessage message) {
        getHttpServer().startServer();

        return this;
    }
}
