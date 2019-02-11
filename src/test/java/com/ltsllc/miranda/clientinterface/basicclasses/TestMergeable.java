package com.ltsllc.miranda.clientinterface.basicclasses;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

public class TestMergeable extends TestCase {
    private Mergeable mergeable1;
    private Mergeable mergeable2;

    public Mergeable getMergeable1() {
        return mergeable1;
    }

    public void setMergeable1(Mergeable mergeable1) {
        this.mergeable1 = mergeable1;
    }

    public Mergeable getMergeable2() {
        return mergeable2;
    }

    public void setMergeable2(Mergeable mergeable2) {
        this.mergeable2 = mergeable2;
    }

    @Before
    public void setup () throws Exception {
        mergeable1 = new Event(Event.Methods.POST, "01");
        mergeable1.setLastChange(new Long(1));
        mergeable2 = new Event(Event.Methods.PUT, "02");
        mergeable2.setLastChange(new Long(2));
    }

    @Test
    public void testMerge1 () {
        boolean result = getMergeable1().merge(getMergeable2());
        assert (result);
        assert (getMergeable1().getLastChange() == getMergeable2().getLastChange());
    }

    @Test
    public void testMege2 () {
        boolean result = getMergeable2().merge(getMergeable1());
        assert (!result);
    }

    @Test
    public void testChangedAfter () {
        assert(getMergeable2().changedAfter(getMergeable1()));
    }
}
