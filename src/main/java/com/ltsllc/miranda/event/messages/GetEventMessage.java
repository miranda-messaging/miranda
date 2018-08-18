package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * A message asking for an {@link com.ltsllc.miranda.clientinterface.basicclasses.Event}
 */
public class GetEventMessage extends Message {
    private String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public GetEventMessage (String eventId, BlockingQueue<Message> senderQueue, Object senderObject)
    {
        super(Subjects.GetEvent, senderQueue, senderObject);
        setEventId(eventId);
    }
}
