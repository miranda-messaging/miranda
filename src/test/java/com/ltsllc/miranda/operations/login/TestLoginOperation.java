package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestLoginOperation extends TestCase {
    private LoginOperation loginOperation;

    public LoginOperation getLoginOperation() {
        return loginOperation;
    }

    public void reset () {
        super.reset();

        loginOperation = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        loginOperation = new LoginOperation("whatever", new LinkedBlockingQueue<Message>());
    }

    @Test
    public void testConstructor () {
        assert (getLoginOperation().getUser().equals("whatever"));
        assert (getLoginOperation().getCurrentState() instanceof LoginOperationReadyState);
    }
}
