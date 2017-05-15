package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.messages.CheckSessionResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestServletHolderReadyState extends TestCase {
    private ServletHolderReadyState readyState;

    public ServletHolderReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        readyState = new ServletHolderReadyState(getMockServletHolder());
    }

    @Test
    public void testCheckSessionResponseMethodSessionExists () {
        CheckSessionResponseMessage checkSessionResponseMessage = new CheckSessionResponseMessage(null, this,
                Results.Success, getMockSession());

        when(getMockServletHolder().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(checkSessionResponseMessage);

        assert (nextState == getReadyState());
        verify (getMockServletHolder(), atLeastOnce()).setSessionAndAwaken(Matchers.any(Session.class));
    }
}
