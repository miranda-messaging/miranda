package com.ltsllc.miranda.mina;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.*;
import com.ltsllc.miranda.network.messages.CloseMessage;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.util.concurrent.BlockingQueue;

public class MinaHandle extends Handle {
    private static Gson ourGson = new Gson();

    private Network network;
    private MinaHandler minaHandler;

    public MinaHandle (MinaHandler minaHandler, BlockingQueue<Message> queue) {
        super(queue);

        this.minaHandler = minaHandler;
    }

    public Network getNetwork() {
        return network;
    }

    public MinaHandler getMinaHandler() {
        return minaHandler;
    }

    public void close () {
        getMinaHandler().close();
    }

    public void close (CloseMessage closeMessage) {
        close();
    }

    public void send (SendNetworkMessage sendNetworkMessage) {
        getMinaHandler().sendOnWire(sendNetworkMessage.getWireMessage());
    }

    public void panic () {
        close();
    }

    public void newMessage (WireMessage wireMessage) {
        deliver(wireMessage);
    }
}
