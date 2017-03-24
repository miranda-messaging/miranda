package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.messages.ConnectFailedMessage;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.node.messages.RetryMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestRetryingState extends TesterNodeState {
    private RetryingState retryingState;

    public RetryingState getRetryingState() {
        return retryingState;
    }

    public void reset () {
        super.reset();

        retryingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        retryingState = new RetryingState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testStart () {
        setupMockTimer();

        State nextState = getRetryingState().start();

        assert (nextState instanceof RetryingState);
        verify(getMockTimer(), atLeastOnce()).sendScheduleOnce(Matchers.anyLong(), Matchers.any(BlockingQueue.class),
                Matchers.any(RetryMessage.class));
    }

    @Test
    public void testProcessRetryMessage () {
        RetryMessage retryMessage = new RetryMessage(null, this);

        when(getMockNode().getQueue()).thenReturn(null);
        when(getMockNode().getDns()).thenReturn("foo.com");
        when(getMockNode().getPort()).thenReturn(6789);

        State nextState = getRetryingState().processMessage(retryMessage);

        assert (nextState instanceof RetryingState);

        verify(getMockNetwork(), atLeastOnce()).sendConnect(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq("foo.com"), Matchers.eq(6789));
    }

    @Test
    public void testProcessConnectSucceeded () {
        ConnectSucceededMessage connectSucceededMessage = new ConnectSucceededMessage(null, this, 13);

        State nextState = getRetryingState().processMessage(connectSucceededMessage);

        assert (nextState instanceof JoiningState);
        verify (getMockNode(), atLeastOnce()).setHandle(Matchers.eq(13));
    }

    @Test
    public void testProcessConnectFailed () {
        setupMockTimer();
        ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, this, null);

        int initialCount = getRetryingState().getRetryCount();
        State nextState = getRetryingState().processMessage(connectFailedMessage);
        int finalCount = getRetryingState().getRetryCount();

        assert (nextState instanceof RetryingState);

        assert (finalCount == 1 + initialCount);

        verify(getMockTimer(), atLeastOnce()).sendScheduleOnce(Matchers.anyLong(), Matchers.any(BlockingQueue.class),
                Matchers.any(RetryMessage.class));
    }

    @Test
    public void testProcessConnectFailedMaxTime () {
        setupMockTimer();
        ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, this, null);

        getRetryingState().setRetryCount(20);
        State nextState = getRetryingState().processMessage(connectFailedMessage);

        assert (nextState instanceof RetryingState);

        verify(getMockTimer(), atLeastOnce()).sendScheduleOnce(Matchers.eq(RetryingState.MAX_TIME), Matchers.any(BlockingQueue.class),
                Matchers.any(RetryMessage.class));
    }

    @Test
    public void testProcessConnectFailedDelayDoubles () {
        setupMockTimer();
        ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, this, null);

        getRetryingState().setRetryCount(0);
        State nextState = getRetryingState().processMessage(connectFailedMessage);

        assert (nextState instanceof RetryingState);

        verify(getMockTimer(), atLeastOnce()).sendScheduleOnce(Matchers.eq(RetryingState.INITIAL_DELAY), Matchers.any(BlockingQueue.class),
                Matchers.any(RetryMessage.class));

        nextState = getRetryingState().processMessage(connectFailedMessage);

        assert (nextState instanceof RetryingState);

        verify(getMockTimer(), atLeastOnce()).sendScheduleOnce(Matchers.eq(2 * RetryingState.INITIAL_DELAY),
                Matchers.any(BlockingQueue.class), Matchers.any(RetryMessage.class));
    }

}
