package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

public class BootstrapResponseMessage extends Message {
    private Results result;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public BootstrapResponseMessage(BlockingQueue<Message> senderQueue, Object senderObject, Results result) {
        super (Subjects.BootstrapResponse, senderQueue, senderObject);
        setResult(result);
    }
}
