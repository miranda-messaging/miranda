package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/10/2017.
 */
public class MinaIncomingHandle extends Handle {
    private MinaIncomingHandler handler;

    public MinaIncomingHandler getHandler () {
        return handler;
    }

    public MinaIncomingHandle (BlockingQueue<Message> notify, MinaIncomingHandler handler) {
        super(notify);

        this.handler = handler;
    }

    public void close () {
        getHandler().close();
    }

    public void panic () {
        close();
    }

    public void send (SendNetworkMessage sendNetworkMessage) {
        getHandler().send(sendNetworkMessage.getWireMessage());
    }
}
