package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

public class NewUserResponseMessage extends Message {
    private Throwable exception;
    private Results result;

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public NewUserResponseMessage (Results result, BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.NewUserResponse, senderQueue, senderObject);
        setResult(result);
    }

    public NewUserResponseMessage (Results result, Throwable exception, BlockingQueue<Message> senderQueue,
                                   Object senderObject)
    {
        super(Subjects.NewUserResponse, senderQueue, senderObject);
        setResult(result);
        setException(exception);
    }
}
