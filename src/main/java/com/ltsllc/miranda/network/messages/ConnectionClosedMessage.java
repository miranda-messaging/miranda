package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */
public class ConnectionClosedMessage extends Message {
    public ConnectionClosedMessage(BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.ConnectionClosed, senderQueue, sender);
    }
}
