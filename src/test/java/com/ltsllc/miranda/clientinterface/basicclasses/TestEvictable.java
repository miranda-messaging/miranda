package com.ltsllc.miranda.clientinterface.basicclasses;

import org.junit.Test;

public class TestEvictable {
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Test
    public void testCanBeEvicted () {
        assert (getEvent().canBeEvicted());
    }
}
