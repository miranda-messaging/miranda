package com.ltsllc.miranda.util;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;

/**
 * Created by Clark on 3/25/2017.
 */
public class TestImprovedRandom extends TestCase {
    private ImprovedRandom improvedRandom;

    public ImprovedRandom getImprovedRandom() {
        return improvedRandom;
    }

    public void reset () {
        super.reset();

        improvedRandom = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        SecureRandom secureRandom = new SecureRandom();
        improvedRandom = new ImprovedRandom(secureRandom);
    }

    @Test
    public void testNextIndexLength () {
        int index = getImprovedRandom().nextIndex(11);

        assert (index < 11 && index >= 0);
    }

    @Test
    public void testNextIndexArray () {
        Integer[] testArray = new Integer[11];

        int index = getImprovedRandom().nextIndex(testArray);

        assert (index >= 0 && index < 11);
    }

    @Test
    public void testNextByte () {
        int i = getImprovedRandom().nextByte();

        assert (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE);
    }

    @Test
    public void testNextNonNegativeLong () {
        long value = 0;

        while (!getImprovedRandom().getWasNegative()) {
            value = getImprovedRandom().nextNonNegativeLong();
        }

        assert (value > 0);
    }
}
