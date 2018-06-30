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

package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/23/2017.
 */
public class HandleHolder extends Consumer {
    private BlockingQueue<Message> recipient;
    private Handle handle;

    public Handle getHandle() {
        return handle;
    }

    public BlockingQueue<Message> getRecipient() {
        return recipient;
    }

    public HandleHolder(Handle handle, BlockingQueue<Message> recipient) {
        super();
        this.handle = handle;
        this.recipient = recipient;
    }

    public void newMessage(WireMessage wireMessage) {
        NetworkMessage networkMessage = new NetworkMessage(getQueue(), this, wireMessage);
        send(networkMessage, getRecipient());
    }

    public void sendNetworkMessage(SendNetworkMessage sendNetworkMessage) {

    }
}
