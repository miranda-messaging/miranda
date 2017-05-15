package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        setuplog4j();

        loginOperation = new LoginOperation("whatever", new LinkedBlockingQueue<Message>());
    }

    @Test
    public void testConstructor () {
        assert (getLoginOperation().getUser().equals("whatever"));
        assert (getLoginOperation().getCurrentState() instanceof LoginOperationReadyState);
    }

    @Test
    public void testStart () {
        setupMockMiranda();
        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());

        getLoginOperation().start();

        verify(getMockSessionManager(), atLeastOnce()).sendGetSessionMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.anyString());
    }
}
