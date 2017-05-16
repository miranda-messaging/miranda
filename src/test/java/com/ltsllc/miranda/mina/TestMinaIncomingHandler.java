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

import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.test.TestCase;
import org.apache.mina.core.session.IoSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/17/2017.
 */
public class TestMinaIncomingHandler extends TestCase {
    @Mock
    private IoSession mockSession;

    @Mock
    private Handle mockHandle;

    private BlockingQueue<Handle> handleQueue;
    private MinaIncomingHandler minaIncomingHandler;

    public MinaIncomingHandler getMinaIncomingHandler() {
        return minaIncomingHandler;
    }

    public BlockingQueue<Handle> getHandleQueue() {
        return handleQueue;
    }

    public IoSession getMockIoSession() {
        return mockSession;
    }

    public Handle getMockHandle() {
        return mockHandle;
    }

    public void reset () {
        super.reset();

        mockSession = null;
        mockHandle = null;
        handleQueue = null;
        minaIncomingHandler = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockSession = mock(IoSession.class);
        mockHandle = mock(Handle.class);
        handleQueue = new LinkedBlockingQueue<Handle>();
        minaIncomingHandler = new MinaIncomingHandler(handleQueue);
    }

    @Test
    public void testSessionCreated () {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("foo.com", 6789);
        Handle handle = null;

        when(getMockIoSession().getRemoteAddress()).thenReturn(inetSocketAddress);

        try {
            getMinaIncomingHandler().sessionCreated(getMockIoSession());
            handle = getHandleQueue().take();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert (null != handle);
    }

    public static final String TEST_MESSAGE = "{ \"wireSubject\" : \"Join\", \"dns\" : \"foo.com\", \"ip\" : \"192.168.1.1\", "
            + "\"port\" : 6789, \"description\" : \"a node\", \"className\" : \"com.ltsllc.miranda.node.networkMessages.JoinWireMessage\"}";

    @Test
    public void testMessagReceived () {
        String message = TEST_MESSAGE;

        getMinaIncomingHandler().setHandle(getMockHandle());
        getMinaIncomingHandler().setSession(getMockIoSession());

        try {
            getMinaIncomingHandler().messageReceived(getMockIoSession(), message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Mockito.verify(getMockHandle(), atLeastOnce()).deliver(Matchers.any(WireMessage.class));
    }
}
