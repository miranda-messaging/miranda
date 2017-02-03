package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class ConnectFailedMessage extends Message {
    private Throwable cause;

    public ConnectFailedMessage(BlockingQueue<Message> sender, Throwable cause) {
        super(Subjects.ConnectionError, sender);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
