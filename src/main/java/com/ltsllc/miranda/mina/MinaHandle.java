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
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.messages.CloseMessage;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.sun.xml.internal.stream.util.BufferAllocator;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.BlockingQueue;

public class MinaHandle extends Handle {
    private static BufferAllocator bufferAllocator = new BufferAllocator();

    private IoSession ioSession;

    public IoSession getIoSession() {
        return ioSession;
    }

    public static BufferAllocator getBufferAllocator() {
        return bufferAllocator;
    }

    public MinaHandle(IoSession ioSession, BlockingQueue<Message> queue) {
        super(queue);
        this.ioSession = ioSession;
    }

    public void send(SendNetworkMessage sendNetworkMessage) throws NetworkException {
        char[] jsonArray = sendNetworkMessage.toJson().toCharArray();
        char[] buffer = getBufferAllocator().getCharBuffer(jsonArray.length);
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = jsonArray[i];
        }
    }

    public void close() {
        ioSession.closeNow();
    }

    public void panic() {
        close();
    }


}
