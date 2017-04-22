package com.ltsllc.miranda.event;

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.ImprovedRandom;
import com.ltsllc.miranda.util.Utils;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;

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

        super.setup();

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

    @Test
    public void testUpdateFrom () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        Event event = Event.createRandom(improvedRandom, 1024);
        IllegalStateException illegalStateException = null;

        try {
            event.updateFrom(event);
        } catch (IllegalStateException e) {
            illegalStateException = e;
        }

        assert (illegalStateException != null);
    }

    @Test
    public void testMatch () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        Event event = Event.createRandom(improvedRandom, 1024);
        Event other = Event.createRandom(improvedRandom, 1024);

        assert (event.matches(event));
        assert (!event.matches(other));
    }
}
