package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/1/2017.
 */
public class DisconnectedMessage extends Message {
    public DisconnectedMessage(BlockingQueue<Message> senderQueue, Object sender) {
        super (Subjects.Disconnected, senderQueue, sender);
    }
}
