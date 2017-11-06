/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.network.messages.ConnectFailedMessage;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.node.messages.RetryMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestRetryingState extends TesterNodeState {
    private RetryingState retryingState;

    public RetryingState getRetryingState() {
        return retryingState;
    }

    public void reset () throws MirandaException {
        super.reset();

        retryingState = null;
    }

    @Before
    public void setup () throws MirandaException {
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
    public void testProcessRetryMessage () throws MirandaException {
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
    public void testProcessConnectSucceeded () throws MirandaException {
        ConnectSucceededMessage connectSucceededMessage = new ConnectSucceededMessage(null, this, 13);

        State nextState = getRetryingState().processMessage(connectSucceededMessage);

        assert (nextState instanceof JoiningState);
        verify (getMockNode(), atLeastOnce()).setHandle(Matchers.eq(13));
    }

    @Test
    public void testProcessConnectFailed () throws MirandaException {
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
    public void testProcessConnectFailedMaxTime () throws MirandaException {
        setupMockTimer();
        ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, this, null);

        getRetryingState().setRetryCount(20);
        State nextState = getRetryingState().processMessage(connectFailedMessage);

        assert (nextState instanceof RetryingState);

        verify(getMockTimer(), atLeastOnce()).sendScheduleOnce(Matchers.eq(RetryingState.MAX_TIME), Matchers.any(BlockingQueue.class),
                Matchers.any(RetryMessage.class));
    }

    @Test
    public void testProcessConnectFailedDelayDoubles () throws MirandaException {
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
