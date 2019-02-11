package com.ltsllc.miranda.clientinterface.basicclasses;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestDeadLetterQueue extends TestCase {
    private DeadLetterQueue deadLetterQueue;

    public DeadLetterQueue getDeadLetterQueue() {
        return deadLetterQueue;
    }

    public void setDeadLetterQueue(DeadLetterQueue deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();
        deadLetterQueue = new DeadLetterQueue();
    }

    public void reset () throws Exception{
        super.reset();
    }

    @Test
    public void testIsEqivalentTo () throws IOException {
        assert (getDeadLetterQueue().isEquivalentTo(getDeadLetterQueue()));
        DeadLetterQueue other = new DeadLetterQueue();
        Event event = new Event(Event.Methods.POST,"01");
        other.addEvent(event);
        assert (!getDeadLetterQueue().isEquivalentTo(other));
    }

    @Test
    public void testCopyFrom () throws IOException {
        DeadLetterQueue deadLetterQueue = new DeadLetterQueue();
        assert (deadLetterQueue.isEquivalentTo(getDeadLetterQueue()));
        Event event = new Event(Event.Methods.POST, "01");
        deadLetterQueue.addEvent(event);
        assert (!getDeadLetterQueue().isEquivalentTo(deadLetterQueue));
        getDeadLetterQueue().copyFrom(deadLetterQueue);
        assert (getDeadLetterQueue().isEquivalentTo(deadLetterQueue));
    }

}
