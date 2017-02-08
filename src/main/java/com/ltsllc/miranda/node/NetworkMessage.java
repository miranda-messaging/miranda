package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class NetworkMessage extends Message {
    private WireMessage wireMessage;

    public WireMessage getWireMessage() {
        return wireMessage;
    }

    public NetworkMessage (BlockingQueue<Message> senderQueue, Object sender, WireMessage wireMessage) {
        super(Subjects.NetworkMessage, senderQueue, sender);
        this.wireMessage = wireMessage;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
