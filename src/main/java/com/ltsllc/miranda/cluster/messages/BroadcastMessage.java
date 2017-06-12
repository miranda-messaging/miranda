package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by clarkhobbie on 6/12/17.
 */
public class BroadcastMessage extends Message {
    private WireMessage wireMessage;

    public WireMessage getWireMessage() {
        return wireMessage;
    }

    public BroadcastMessage(BlockingQueue<Message> sender, Object senderObject, WireMessage wireMessage) {
        super(Subjects.Broadcast, sender, senderObject);
        this.wireMessage = wireMessage;
    }
}
