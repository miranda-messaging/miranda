package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

public class WriteResponseMessage extends Message {
    private String filename;
    private Results result;
    private Throwable exception;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

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

    public WriteResponseMessage(String filename, Results result, BlockingQueue<Message> queue, Object senderObject) {
        super(Subjects.WriteResponse, queue, senderObject);
        setResult(result);
        setFilename(filename);
    }

    public WriteResponseMessage(String filename, Results result, Throwable exception, BlockingQueue<Message> senderQueue,
                                Object senderObject)
    {
        super(Subjects.WriteResponse, senderQueue, senderObject);
        setResult(result);
        setFilename(filename);
        setException(exception);
    }
}
