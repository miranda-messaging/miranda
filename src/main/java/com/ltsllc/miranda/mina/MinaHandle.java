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

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.CloseMessage;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;

import java.util.concurrent.BlockingQueue;

public class MinaHandle extends Handle {
    private MinaHandler minaHandler;

    public MinaHandle (MinaHandler minaHandler, BlockingQueue<Message> queue) {
        super(queue);

        this.minaHandler = minaHandler;
    }

    public MinaHandler getMinaHandler() {
        return minaHandler;
    }

    public void close () {
        getMinaHandler().close();
    }

    public void close (CloseMessage closeMessage) {
        close();
    }

    public void send (SendNetworkMessage sendNetworkMessage) {
        getMinaHandler().sendOnWire(sendNetworkMessage.getWireMessage());
    }

    public void panic () {
        close();
    }
}
