package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class JoinSuccessMessage extends NetworkMessage {
    public JoinSuccessMessage (BlockingQueue<Message> queue, WireMessage wireMessage)
    {
        super(queue, wireMessage);
    }
}
