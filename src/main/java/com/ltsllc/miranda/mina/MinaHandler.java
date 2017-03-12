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

    public MinaHandle getMinaHandle() {
        return minaHandle;
    }

    public void setMinaHandle (MinaHandle minaHandle) {
        this.minaHandle = minaHandle;
    }


    @Override
    public void sessionCreated(IoSession session) throws Exception {
        this.session = session;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String json = message.toString();
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
        getSession().write(json);
    }
}
