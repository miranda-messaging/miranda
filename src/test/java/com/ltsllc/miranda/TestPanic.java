package com.ltsllc.miranda;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestPanic extends TestCase {
    private Panic panic;

    public Panic getPanic() {
        return panic;
    }

    public void reset () {
        panic = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        panic = new Panic("A test panic", null, Panic.Reasons.Test);
    }

    @Test
    public void testConstructor () {
        assert (getPanic().getReason() == Panic.Reasons.Test);
        assert (getPanic().getMessage().equals("A test panic"));
        assert (getPanic().getCause() == null);
    }
}
