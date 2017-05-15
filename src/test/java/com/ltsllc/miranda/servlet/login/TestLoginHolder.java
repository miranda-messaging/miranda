package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestLoginHolder extends TestCase {
    private LoginHolder loginHolder;

    public LoginHolder getLoginHolder() {
        return loginHolder;
    }

    public void reset () {
        super.reset();

        loginHolder = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        loginHolder = new LoginHolder(1000);
    }

    @Test
    public void testConstructor () {
        assert (getLoginHolder().getCurrentState() instanceof LoginHolderReadyState);
    }

    @Test
    public void testLoginSuccess () {
        TimeoutException timeoutException = null;

        try {
            LoginHolder.LoginResult loginResult = getLoginHolder().login("whatever");
            getLoginHolder().setSession(getMockSession());
            getLoginHolder().setResult(Results.Success);
        } catch (TimeoutException e) {
            timeoutException = e;
        }

        assert (null == timeoutException);
    }
}
