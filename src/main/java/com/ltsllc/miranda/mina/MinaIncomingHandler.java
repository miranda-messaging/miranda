package com.ltsllc.miranda.mina;

import com.google.gson.Gson;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.lang.reflect.Type;

/**
 * Created by Clark on 3/10/2017.
 */
public class MinaIncomingHandler extends IoHandlerAdapter {
    private static Gson ourGson = new Gson();

    private IoSession session;
    private MinaNetwork minaNetwork;
    private MinaIncomingHandle minaIncomingHandle;

    public MinaIncomingHandle getMinaIncomingHandle() {
        return minaIncomingHandle;
    }

    public MinaNetwork getMinaNetwork() {
        return minaNetwork;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        setSession(session);

        MinaIncomingHandle minaIncomingHandle = getMinaNetwork().newConnection(this);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String json = message.toString();
        WireMessage pass1 = ourGson.fromJson(json, WireMessage.class);
        Type type = getClass().forName(pass1.getClassName());
        WireMessage wireMessage = ourGson.fromJson(json, type);

        getMinaIncomingHandle().deliver(wireMessage);
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
