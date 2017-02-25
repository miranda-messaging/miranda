package com.ltsllc.miranda.event;

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.Utils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 2/19/2017.
 */

public class TestEvent extends TestCase {
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void reset () {
        this.event = null;
    }

    @Before
    public void setup () {
        reset();

        this.event = new Event(Event.Methods.POST, "junk");
    }

    @Test
    public void testConstructors () {
        String junk = "junk";
        assert (getEvent().getContent().equals(junk));
        assert (getEvent().getMethod() == Event.Methods.POST);

        Event event = new Event(Event.Methods.PUT, junk.getBytes());

        assert (event.getMethod() == Event.Methods.PUT);

        String junkAsBinaryAsHex = Utils.bytesToString(junk.getBytes());
        assert (event.getContent().equals(junkAsBinaryAsHex));
    }
}
