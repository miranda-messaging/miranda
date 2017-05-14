package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.UserManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestManager extends TestCase {
    private Manager manager;

    public Manager getManager() {
        return manager;
    }

    public void reset () {
        super.reset();

        manager = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        manager = new UserManager("testFile");
    }

    @Test
    public void testConstructor () {
        assert (getManager().getName().equals(UserManager.NAME));
        assert (getManager().getData() != null);
    }
}
