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

package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.test.TestCase;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/17/2017.
 */
public class TestFutureListener extends TestCase {
    @Mock
    private ConnectFuture mockConnectFuture;

    @Mock
    private Handle mockHandle;

    @Mock
    private IoSession mockSession;

    private BlockingQueue<Message> queue;
    private FutureListener futureListener;

    public FutureListener getFutureListener() {
        return futureListener;
    }

    public BlockingQueue<Message> getQueue() {

        return queue;
    }

    public Handle getMockHandle() {

        return mockHandle;
    }

    public ConnectFuture getMockConnectFuture() {
        return mockConnectFuture;
    }

    public IoSession getMockIoSession() {
        return mockSession;
    }

    public void reset () {
        super.reset();

        mockSession = null;
        mockHandle = null;
        mockConnectFuture = null;
        futureListener = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockSession = mock(IoSession.class);
        mockHandle = mock(Handle.class);
        mockConnectFuture = mock(ConnectFuture.class);
        queue = new LinkedBlockingQueue<Message>();
        futureListener = new FutureListener(queue, -1, getMockNetwork(), mockHandle);
    }

    @Test
    public void testOperationComplete () {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("foo.com", 6789);
        when (getMockConnectFuture().isConnected()).thenReturn(true);
        when (getMockConnectFuture().getSession()).thenReturn(getMockIoSession());
        when (getMockIoSession().getRemoteAddress()).thenReturn(inetSocketAddress);

        getFutureListener().operationComplete(getMockConnectFuture());

        assert (contains(Message.Subjects.ConnectSucceeded, getQueue()));

        setup();

        when(getMockConnectFuture().isConnected()).thenReturn(false);

        getFutureListener().operationComplete(getMockConnectFuture());

        assert (contains(Message.Subjects.ConnectFailed, getQueue()));
    }
}
