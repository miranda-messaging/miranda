package com.ltsllc.miranda.deliveries.messages;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class DeliveryResultMessage extends Message {
    private Results result;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public DeliveryResultMessage(Results result, BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.DeliveryResult, senderQueue, senderObject);
        setResult(result);
    }


}

