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

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/4/2017.
 */
abstract public class HttpServer extends Consumer {
    abstract public void addServlets(List<ServletMapping> servlets);

    abstract public void startServer();

    public HttpServer() throws MirandaException {
        super("http server");

        HttpReadyState httpReadyState = new HttpReadyState(this);
        setCurrentState(httpReadyState);
    }

    public void sendStart(BlockingQueue<Message> senderQueue) {
        StartHttpServerMessage startHttpServerMessage = new StartHttpServerMessage(senderQueue, this);
        sendToMe(startHttpServerMessage);
    }

    public void sendSetupServletsMessage(BlockingQueue<Message> senderQueue, Object sender,
                                         List<ServletMapping> servletMappings) {
        SetupServletsMessage setupServletsMessage = new SetupServletsMessage(senderQueue, sender, servletMappings);
        sendToMe(setupServletsMessage);
    }
}
