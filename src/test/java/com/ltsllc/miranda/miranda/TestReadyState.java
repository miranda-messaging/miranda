package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionManager;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/5/2017.
 */
public class TestReadyState extends TestCase {
    private ReadyState readyState;

    @Mock
    private SessionManager mockSessionManager;

    public ReadyState getReadyState() {
        return readyState;
    }

    public SessionManager getMockSessionManager() {
        return mockSessionManager;
    }

    public void reset () {
        this.readyState = null;
    }


    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        this.readyState = new ReadyState(getMockMiranda());
        this.mockSessionManager = mock(SessionManager.class);
    }

    @Test
    public void testProcessNewConnectionMessage () {
        Node node = new Node(-1, getMockNetwork(), getMockCluster());
        NewConnectionMessage message = new NewConnectionMessage(null, this, node);

        State nextState = getReadyState().processMessage(message);

        assert (nextState instanceof ReadyState);
        assert (contains(Message.Subjects.GetVersion, node.getQueue()));
    }

    @Test
    public void testProcessAddSessionMessage () {
        Session session = new Session("whatever", 123, 456);
        AddSessionMessage addSessionMessage = new AddSessionMessage(null, this, session);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());

        State nextState = getReadyState().processMessage(addSessionMessage);

        assert (nextState instanceof ReadyState);
        verify(getMockSessionManager(), atLeastOnce()).sendAddSessionMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(session));
    }

    @Test
    public void testProcessSessionsExpired () {
        Session session = new Session("whatever", 123, 456);
        List<Session> expiredSessions = new ArrayList<Session>();
        expiredSessions.add(session);

        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());
        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(null, this, expiredSessions);

        State nextState = getReadyState().processMessage(sessionsExpiredMessage);

        assert (nextState instanceof ReadyState);
        verify (getMockSessionManager(), atLeastOnce()).sendSessionsExpiredMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(expiredSessions));
    }
}
