package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class GetEventReplyMessage extends Message {
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

    public GetEventReplyMessage (Results result, Event event, BlockingQueue<Message> senderQueque, Object senderObject) {
        super (Subjects.GetEventReply, senderQueque, senderObject);

        setEvent(event);
        setResult(result);

    }
}
