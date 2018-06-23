package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/7/2017.
 */
public class ReadEventResponseMessage extends Message {
    private Results result;
    private Event event;

    public ReadEventResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result, Event event) {
        super(Message.Subjects.ReadResponse, senderQueue, sender);

        this.event = event;
        this.result = result;
    }

    public Event getEvent() {
        return event;
    }

    public Results getResult() {
        return result;
    }
}
