package com.ltsllc.miranda.deliveries.messages;

import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class ScheduleDeliveryMessage extends Message {
    private Event event;
    private String url;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ScheduleDeliveryMessage(Event event, String url, BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.ScheduleDelivery, senderQueue, senderObject);
        setEvent(event);
        setUrl(url);
    }
}
