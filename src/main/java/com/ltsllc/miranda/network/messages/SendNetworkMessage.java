package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/10/2017.
 */
public class SendNetworkMessage extends Message {
    private int handle;
    private WireMessage wireMessage;

    public WireMessage getWireMessage() {
        return wireMessage;
    }

    public int getHandle() {
        return handle;
    }

    public SendNetworkMessage (BlockingQueue<Message> senderQueue, Object sender, WireMessage wireMessage, int handle) {
        super(Subjects.SendNetworkMessage, senderQueue, sender);

        this.wireMessage = wireMessage;
        this.handle = handle;
    }
}
