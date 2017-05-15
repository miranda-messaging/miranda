package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.messages.CreateSessionResponseMessage;
import com.ltsllc.miranda.session.messages.GetSessionResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestLoginOperationReadyState extends TestCase {
    @Mock
    private LoginOperation mockLoginOperation;

    private LoginOperationReadyState readyState;

    public LoginOperationReadyState getReadyState() {
        return readyState;
    }

    public LoginOperation getMockLoginOperation() {
        return mockLoginOperation;
    }

    public void reset () {
        super.reset();

        mockLoginOperation = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockLoginOperation = mock(LoginOperation.class);
        readyState = new LoginOperationReadyState(mockLoginOperation);
    }

    @Test
    public void testGetSessionResponseMessageSessionCreated () {
        BlockingQueue<Message> temp = new LinkedBlockingQueue<Message>();
        setupMockMiranda();
        GetSessionResponseMessage getSessionResponseMessage = new GetSessionResponseMessage(null, this,
                Results.SessionCreated, getMockSession());
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when(getMockLoginOperation().getRequester()).thenReturn(temp);

        State nextState = getReadyState().processMessage(getSessionResponseMessage);

        assert (nextState == StopState.getInstance());
        verify(getMockCluster(), atLeastOnce()).sendNewSession(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.any(Session.class));
        assert (contains(Message.Subjects.LoginResponse, temp));
    }

    @Test
    public void testGetSessionResponseMessageSessionExisted () {
        BlockingQueue<Message> temp = new LinkedBlockingQueue<Message>();
        setupMockMiranda();
        GetSessionResponseMessage getSessionResponseMessage = new GetSessionResponseMessage(null, this,
                Results.Success, getMockSession());
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when(getMockLoginOperation().getRequester()).thenReturn(temp);

        State nextState = getReadyState().processMessage(getSessionResponseMessage);

        assert (nextState == StopState.getInstance());
        verify(getMockCluster(), never()).sendNewSession(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.any(Session.class));
        assert (contains(Message.Subjects.LoginResponse, temp));
    }

    @Test
    public void testGetSessionResponseMessageException () {
        BlockingQueue<Message> temp = new LinkedBlockingQueue<Message>();
        setupMockMiranda();
        GetSessionResponseMessage getSessionResponseMessage = new GetSessionResponseMessage(null, this,
                Results.Exception, getMockSession());
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when(getMockLoginOperation().getRequester()).thenReturn(temp);

        State nextState = getReadyState().processMessage(getSessionResponseMessage);

        assert (nextState == StopState.getInstance());
        verify(getMockCluster(), never()).sendNewSession(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.any(Session.class));
        assert (contains(Message.Subjects.LoginResponse, temp));
    }
}
