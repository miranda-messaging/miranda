package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;

/**
 * Created by Clark on 4/15/2017.
 */
public class TestUserHolder extends TestCase {
    private UserHolder userHolder;

    public UserHolder getUserHolder() {
        return userHolder;
    }

    public void reset () {
        super.reset();

        userHolder = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        userHolder = new UserHolder(1000);
    }
}
