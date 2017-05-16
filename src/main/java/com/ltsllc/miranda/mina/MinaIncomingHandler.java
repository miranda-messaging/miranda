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
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/10/2017.
 */
public class MinaIncomingHandler extends IoHandlerAdapter {
    private static Gson ourGson = new Gson();
    private static Logger logger = Logger.getLogger(MinaIncomingHandler.class);

    private IoSession session;
    private Handle handle;
    private BlockingQueue<Handle> queue;

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public Handle getHandle() {
        return handle;
    }

    public void setHandle(Handle handle) {
        this.handle = handle;
    }

    public BlockingQueue<Handle> getQueue() {
        return queue;
    }

    public MinaIncomingHandler (BlockingQueue<Handle> queue) {
        this.queue = queue;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info ("Got connection from " + session.getRemoteAddress());

        setSession(session);

        Handle handle = new MinaIncomingHandle(this);
        setHandle(handle);

        getQueue().put(handle);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String json = message.toString();

        logger.info ("Got " + json);

        WireMessage pass1 = ourGson.fromJson(json, WireMessage.class);
        Type type = getClass().forName(pass1.getClassName());
        WireMessage wireMessage = ourGson.fromJson(json, type);

        getHandle().deliver(wireMessage);
    }

    public void send (WireMessage wireMessage) {
        String json = ourGson.toJson(wireMessage);
        getSession().write(json);
    }

    public void close () {
        if (null != getSession())
        {
            getSession().closeNow();
            setSession(null);
        }
    }
}
