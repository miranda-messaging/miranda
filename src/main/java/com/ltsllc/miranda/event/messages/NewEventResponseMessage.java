package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.event.Event;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/11/2017.
 */
public class NewEventResponseMessage extends Message {
    private Results result;
    private Event event;

    public NewEventResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result, Event event) {
        super(Subjects.NewEventResponse, senderQueue, sender);

        this.event = event;
        this.result = result;
    }

    public NewEventResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result) {
        super(Subjects.NewEventResponse, senderQueue, sender);

        this.result = result;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent (Event event) {
        this.event = event;
    }

    public Results getResult() {

        return result;
    }
}
