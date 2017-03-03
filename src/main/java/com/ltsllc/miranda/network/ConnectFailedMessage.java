package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class ConnectFailedMessage extends Message {
    private Throwable cause;

    public ConnectFailedMessage(BlockingQueue<Message> sender, Object senderObject, Throwable cause) {
        super(Subjects.ConnectionError, sender, senderObject);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
