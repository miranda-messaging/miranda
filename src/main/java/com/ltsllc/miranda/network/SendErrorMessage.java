package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/1/2017.
 */
public class SendErrorMessage extends Message {
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    public SendErrorMessage (BlockingQueue<Message> senderQueue, Object sender, Throwable cause) {
        super(Subjects.SendError, senderQueue, sender);

        this.cause = cause;
    }
}
