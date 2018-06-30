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

package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/10/2017.
 */
public class SendNetworkMessage extends Message {
    private int handle;
    private WireMessage wireMessage;

    public WireMessage getWireMessage() {
        return wireMessage;
    }

    public int getHandle() {
        return handle;
    }

    public SendNetworkMessage(BlockingQueue<Message> senderQueue, Object sender, WireMessage wireMessage, int handle) {
        super(Subjects.SendNetworkMessage, senderQueue, sender);

        this.wireMessage = wireMessage;
        this.handle = handle;
    }
}
