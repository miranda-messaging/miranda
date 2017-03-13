package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/12/2017.
 */
public class NetworkErrorMessage extends Message {
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    public NetworkErrorMessage (BlockingQueue<Message> senderQueue, Object sender, Throwable cause) {
        super(Subjects.NetworkError, senderQueue, sender);

        this.cause = cause;
    }
}
