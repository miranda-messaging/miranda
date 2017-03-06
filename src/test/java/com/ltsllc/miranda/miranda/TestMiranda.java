package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 3/4/2017.
 */
public class TestMiranda extends TestCase {
    private Miranda miranda;

    public Miranda getMiranda() {
        return miranda;
    }

    public void reset () {
        super.reset();

        this.miranda = null;
    }

    @Before
    public void setup () {
        String[] empty = new String[0];

        miranda = new Miranda(empty);
    }

    @Test
    public void testReset () {
        setuplog4j();
        
        getMiranda().reset();

        assert (Miranda.properties == null);
        assert (Miranda.fileWatcher == null);
        assert (Miranda.timer == null);
    }
}
