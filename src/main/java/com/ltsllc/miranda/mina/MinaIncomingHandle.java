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

package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/10/2017.
 */
public class MinaIncomingHandle extends Handle {
    private MinaIncomingHandler handler;

    public MinaIncomingHandler getHandler () {
        return handler;
    }

    public MinaIncomingHandle (BlockingQueue<Message> notify, MinaIncomingHandler handler) {
        super(notify);

        this.handler = handler;
    }

    public MinaIncomingHandle (MinaIncomingHandler handler) {
        super(null);

        this.handler = handler;
    }

    public void close () {
        getHandler().close();
    }

    public void panic () {
        close();
    }

    public void send (WireMessage wireMessage) {
        getHandler().send(wireMessage);
    }
}
