package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestSystemDeliveriesFile extends TestCase {
    private SystemDeliveriesFile systemDeliveriesFile;

    public SystemDeliveriesFile getSystemDeliveriesFile() {
        return systemDeliveriesFile;
    }

    @Before
    public void setup () {
        reset();

        this.systemDeliveriesFile = new SystemDeliveriesFile("deliveries", getWriter());
    }

    @Test
    public void testIsFileOfInterest () {
        String interesting = "20170220-001.msg";
        String notInteresting = "whatever.txt";

        assert (getSystemDeliveriesFile().isFileOfInterest(interesting));
        assert (!getSystemDeliveriesFile().isFileOfInterest(notInteresting));
    }
}


