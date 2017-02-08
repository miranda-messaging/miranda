package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class JoinSuccessMessage extends NetworkMessage {
    public JoinSuccessMessage (BlockingQueue<Message> senderQueue, Object sender, WireMessage wireMessage)
    {
        super(senderQueue, senderQueue, wireMessage);
    }
}
