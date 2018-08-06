package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.clientinterface.basicclasses.Event;


public class DeliveryAttempt
{
    private String id;
    private String event;
    private long time;
    private int tries;

    public DeliveryAttempt (Event event)
    {
        setEvent(event.getGuid());
        setTime(-1);
        setTries(0);
    }

    public DeliveryAttempt (DeliveryAttempt other) {
        setId(new String(other.getId()));
        setEvent(other.getEvent());
        setTime(other.getTime());
        setTries(other.getTries());
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

}
