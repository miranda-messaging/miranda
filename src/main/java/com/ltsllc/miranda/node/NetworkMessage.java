package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class NetworkMessage extends Message {
    private WireMessage wireMessage;

    public NetworkMessage (BlockingQueue<Message> queue, WireMessage wireMessage) {
        super(Subjects.NetworkMessage, queue);
        this.wireMessage = wireMessage;
    }
}
