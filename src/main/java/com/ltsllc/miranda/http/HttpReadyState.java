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

package com.ltsllc.miranda.http;

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

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
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
