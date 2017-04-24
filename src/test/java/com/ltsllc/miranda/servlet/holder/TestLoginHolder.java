package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.login.LoginHolder;
import com.ltsllc.miranda.servlet.login.LoginObject;
import com.ltsllc.miranda.servlet.login.LoginResult;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/15/2017.
 */
public class TestLoginHolder extends TestCase {
    private LoginHolder loginHolder;

    public LoginHolder getLoginHolder() {
        return loginHolder;
    }

    public void setLoginHolder(LoginHolder loginHolder) {
        this.loginHolder = loginHolder;
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

    public static class LocalRunner implements Runnable {
        private String name;
        private TestLoginHolder testLoginHolder;
        public LoginHolder.LoginResult loginResult;
        private LoginObject loginObject;
        public TimeoutException timeoutException;

        public LoginObject getLoginObject() {
            return loginObject;
        }

        public TestLoginHolder getTestLoginHolder() {
            return testLoginHolder;
        }

        public String getName() {
            return name;
        }

        public LocalRunner (TestLoginHolder testLoginHolder, LoginObject loginObject, String name) {
            this.testLoginHolder = testLoginHolder;
            this.loginObject = loginObject;
            this.name = name;
        }

        public void run () {
            timeoutException = null;

            try {
                loginResult = testLoginHolder.getLoginHolder().login(getName());
            } catch (TimeoutException e) {
                timeoutException = e;
            }
        }
    }

    @Test
    public void testLoginTimeout () {
        setupMockMiranda();

        TimeoutException timeoutException = null;

        LoginObject loginObject = new LoginObject("whatever");

        LocalRunner localRunner = new LocalRunner(this, loginObject, "whatever");
        Thread thread = new Thread(localRunner);
        thread.start();

        pause(1500);

        timeoutException = localRunner.timeoutException;

        assert (timeoutException != null);
    }

    @Test
    public void testLoginUserNotFound () {
        setupMockMiranda();

        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());

        LoginObject loginObject = new LoginObject("whatever");

        LocalRunner localRunner = new LocalRunner(this, loginObject, "whatever");
        Thread thread = new Thread(localRunner);
        thread.start();

        pause(100);

        LoginHolder.LoginResult loginResult = new LoginHolder.LoginResult(Results.UserNotFound, null);
        getLoginHolder().setResultAndWakeup(loginResult);

        pause(100);

        loginResult = localRunner.loginResult;

        assert (loginResult.session == null);
        assert (loginResult.result == Results.UserNotFound);
    }

    public static final String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpjR9MH5cTEPIXR/0cLp/Lw3QDK4RMPIygL8Aqh0yQ/MOpQtXrBzwSph4N1NURg1tB3EuyCVGsTfSfrbR5nqsN5IiaJyBuvhThBLwHyKN+PEUQ/rB6qUyg+jcPigTfqj6gksNxnC6CmCJ6XpBOiBOORgFQvdISo7pOqxZKxmaTqwIDAQAB";

    @Test
    public void testLoginSuccess () throws MirandaException {
        setupMockMiranda();

        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());

        LoginObject loginObject = new LoginObject("whatever");


        LocalRunner localRunner = new LocalRunner(this, loginObject, "whatever");
        Thread thread = new Thread(localRunner);
        thread.start();

        pause(100);

        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        long now = System.currentTimeMillis();

        Session session = new Session(user, now, 123);
        LoginHolder.LoginResult loginResult = new LoginHolder.LoginResult(Results.Success, session);

        getLoginHolder().setResultAndWakeup(loginResult);

        pause(100);

        loginResult = localRunner.loginResult;

        assert (loginResult.result == Results.Success);
        assert (loginResult.session == session);
    }
}
