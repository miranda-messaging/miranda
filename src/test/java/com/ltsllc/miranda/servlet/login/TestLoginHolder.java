package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.test.ServletHolderRunner;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestLoginHolder extends TestCase {
    public static class LoginRunner extends ServletHolderRunner {
        private LoginHolder.LoginResult result;

        public LoginHolder.LoginResult getResult() {
            return result;
        }

        public LoginHolder getLoginHolder () {
            return (LoginHolder) getServletHolder();
        }

        public LoginRunner (LoginHolder loginHolder) {
            super(loginHolder);
        }

        public void run () {
            try {
                result = getLoginHolder().login("whatever");
            } catch (TimeoutException e) {
                result = new LoginHolder.LoginResult();
                result.result = Results.Timeout;
            }
        }
    }

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
        setupMockMiranda();

        LoginRunner loginRunner = new LoginRunner(getLoginHolder());
        loginRunner.start();

        pause (50);

        LoginHolder.LoginResult result = new LoginHolder.LoginResult();
        result.result = Results.Success;
        result.session = getMockSession();

        getLoginHolder().setResultAndWakeup(result);

        pause(50);

        assert (loginRunner.getResult().session == getMockSession());
        assert (loginRunner.getResult().result == Results.Success);
    }

    @Test
    public void testLoginUserUnrecognized () {
        setupMockMiranda();

        LoginRunner loginRunner = new LoginRunner(getLoginHolder());
        loginRunner.start();

        pause (50);

        LoginHolder.LoginResult result = new LoginHolder.LoginResult();
        result.result = Results.UserNotFound;

        getLoginHolder().setResultAndWakeup(result);

        pause(50);

        assert (loginRunner.getResult().session == null);
        assert (loginRunner.getResult().result == Results.UserNotFound);
    }

    @Test
    public void testLoginTimeout () {
        setupMockMiranda();

        LoginRunner loginRunner = new LoginRunner(getLoginHolder());
        loginRunner.start();

        pause (5000);

        assert (loginRunner.getResult().session == null);
        assert (loginRunner.getResult().result == Results.Timeout);
    }
}