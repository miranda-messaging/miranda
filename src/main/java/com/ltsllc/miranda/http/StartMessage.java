package com.ltsllc.miranda.http;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by miranda on 7/21/2017.
 */
public class StartMessage extends Message {
    public StartMessage(BlockingQueue<Message> sender, Object senderObject) {
        super(Subjects.Start, sender, senderObject);
    }
}
