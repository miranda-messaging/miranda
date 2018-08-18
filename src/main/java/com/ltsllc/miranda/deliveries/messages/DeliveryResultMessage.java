package com.ltsllc.miranda.deliveries.messages;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class DeliveryResultMessage extends Message {
    private Results result;
    private Event event;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public DeliveryResultMessage(Results result, Event event, BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.DeliveryResult, senderQueue, senderObject);
        setResult(result);
        setEvent(event);
    }


}

