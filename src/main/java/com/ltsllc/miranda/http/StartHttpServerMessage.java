package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/10/2017.
 */
public class StartHttpServerMessage extends Message {
    public StartHttpServerMessage(BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.StartHttpServer, senderQueue, sender);
    }
}
