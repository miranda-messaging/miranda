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

package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.session.Session;
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

    public void reset () throws Exception {
        super.reset();

        mockLoginOperation = null;
        readyState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();

        mockLoginOperation = mock(LoginOperation.class);
        readyState = new LoginOperationReadyState(mockLoginOperation);
    }

    @Test
    public void testGetSessionResponseMessageSessionCreated () throws MirandaException {
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
    public void testGetSessionResponseMessageSessionExisted () throws MirandaException {
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
    public void testGetSessionResponseMessageException () throws MirandaException {
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
