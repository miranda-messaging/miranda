package com.ltsllc.miranda.mina;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.CloseMessage;
import com.ltsllc.miranda.network.ClosedMessage;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.SendMessageMessage;
import com.ltsllc.miranda.node.WireMessage;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public class MinaHandle extends Handle {
    private static Gson ourGson = new Gson();

    private class LocalHandler extends IoHandlerAdapter {
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            String json = message.toString();
            WireMessage pass1 = ourGson.fromJson(json, WireMessage.class);
            Type type = getClass().forName(pass1.getClassName());
            WireMessage wireMessage = ourGson.fromJson(json, type);


        }
    }

    private IoSession session;
    private ConnectFuture future;

    public IoSession getSession() {
        return session;
    }

    public ConnectFuture getFuture() {
        return future;
    }

    public MinaHandle (BlockingQueue<Message> queue, ConnectFuture future, IoConnector connector) {
        super(queue);

        this.future = future;

        LocalHandler localHandler = new LocalHandler();
        connector.setHandler(localHandler);
    }

    public MinaHandle (BlockingQueue<Message> queue, IoSession session) {
        super(queue);

        this.session = session;
    }

    public void close () {
        if (null != getSession())
            getSession().closeNow();
    }

    public void close (CloseMessage closeMessage) {
        close();
    }

    public void send (SendMessageMessage sendMessageMessage) {
        if (null == getSession()) {
            getFuture().awaitUninterruptibly();
            this.session = getFuture().getSession();
        }

        getSession().write(sendMessageMessage.getContent());
    }

    public void panic () {
        close();
    }
}
