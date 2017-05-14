package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/13/2017.
 */
public class ExceptionDuringScanMessage extends Message {
    private Throwable throwable;

    public Throwable getThrowable() {
        return throwable;
    }

    public ExceptionDuringScanMessage(BlockingQueue<Message> senderQueue, Object sender, Throwable throwable) {
        super(Subjects.ExceptionDuringScanMessage, senderQueue, sender);

        this.throwable = throwable;
    }
}
