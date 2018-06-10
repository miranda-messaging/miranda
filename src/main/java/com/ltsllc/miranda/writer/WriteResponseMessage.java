package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

public class WriteResponseMessage extends Message {
    private Results result;
    private Throwable exception;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public WriteResponseMessage(Results result, BlockingQueue<Message> queue, Object senderObject) {
        super(Subjects.WriteResponse, queue, senderObject);
        setResult(result);
    }

    public WriteResponseMessage(Results result, Throwable exception, BlockingQueue<Message> senderQueue,
                                Object senderObject)
    {
        super(Subjects.WriteResponse, senderQueue, senderObject);
        setResult(result);
    }
}
