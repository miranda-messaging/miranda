package com.ltsllc.miranda;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 3/28/2017.
 */
public class TestSubsystem extends TestCase {
    private Subsystem subsystem;

    public Subsystem getSubsystem() {
        return subsystem;
    }

    public void reset () {
        super.reset();

        subsystem = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        subsystem = new Consumer("test");
    }

    @Test
    public void testConstructor () {
        assert (getSubsystem().getQueue() != null);
    }
}
