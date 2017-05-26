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
import com.ltsllc.miranda.network.HandleHolder;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/23/2017.
 */
public class NewMinaHandle extends IoHandlerAdapter {
    private HandleHolder handleHolder;
    private IoSession ioSession;

    public IoSession getIoSession() {
        return ioSession;
    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public NewMinaHandle (IoSession ioSession) {
        setIoSession(ioSession);
    }

    public void send (WireMessage wireMessage) {
        wireMessage.toJson();
        getIoSession().write(wireMessage.toJson());
    }

    public void close () {
        getIoSession().closeNow();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

    }
}
