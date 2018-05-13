package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.file.states.FileReadyState;

import java.util.concurrent.BlockingQueue;

public class WriteFileResponseMessage extends Message {
    private Results result;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public WriteFileResponseMessage(BlockingQueue<Message> senderQueue, Object senderObject, Results result) {
        super(Subjects.WriteFileResponse, senderQueue, senderObject);
        setResult(result);
    }
}
