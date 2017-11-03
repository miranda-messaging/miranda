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
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.lang.reflect.Type;

/**
 * Created by Clark on 3/10/2017.
 */
public class MinaHandler extends IoHandlerAdapter {
    private static Gson ourGson = new Gson();
    private static Logger logger = Logger.getLogger(MinaHandler.class);

    private IoSession session;
    private MinaHandle minaHandle;

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public MinaHandle getMinaHandle() {
        return minaHandle;
    }

    public void setMinaHandle (MinaHandle minaHandle) {
        this.minaHandle = minaHandle;
    }


    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info ("Got connection from " + session.getRemoteAddress());
        this.session = session;
    }

    /**
     * Called when a new message is received from the network.
     *
     * <P>
     *     The message is expected to be a JSON string for an object that subclasses
     *     the WireMessage class.
     * </P>
     * 
     * @param session The IoSession the message was received from
     * @param message The message received
     * @throws Exception The method rethrows ClassNotFoundException if a class
     * that matches the className attribute of the message.
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String json = message.toString();

        logger.info("Receieved " + json);

        WireMessage pass1 = ourGson.fromJson(json, WireMessage.class);
        Type type = getClass().forName(pass1.getClassName());
        WireMessage wireMessage = ourGson.fromJson(json, type);

        getMinaHandle().deliver(wireMessage);
    }

    public void close () {
        if (null != getSession())
        {
            getSession().closeNow();
        }
    }

    public void sendOnWire (WireMessage wireMessage) {
        String json = ourGson.toJson(wireMessage);
        logger.info ("Sending " + json);
        getSession().write(json);
    }
}
