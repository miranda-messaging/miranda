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

package com.ltsllc.miranda.network;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.mina.MinaHandle;
import com.ltsllc.miranda.mina.MinaHandler;
import com.ltsllc.miranda.node.networkMessages.ClusterFileWireMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.test.TestCase;
import org.apache.mina.core.session.IoSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestHandle extends TestCase {
    @Mock
    private IoSession mockIoSession;

    @Mock
    private MinaHandler mockMinaHandler;

    private BlockingQueue<Message> queue;

    private Handle handle;

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public Handle getHandle() {
        return handle;
    }

    public MinaHandler getMockMinaHandler() {
        return mockMinaHandler;
    }

    public void reset () throws Exception {
        super.reset();

        queue = null;
        mockMinaHandler = null;
        handle = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        queue = new LinkedBlockingQueue<Message>();
        mockMinaHandler = mock(MinaHandler.class);
        mockIoSession = mock(IoSession.class);
        handle = new MinaHandle(mockIoSession, queue);
    }

    @Test
    public void testDeliver () {
        WireMessage testWireMessage = new ClusterFileWireMessage("whatever".getBytes(), new Version());

        getHandle().deliver(testWireMessage);

        assert (contains(Message.Subjects.NetworkMessage, getQueue()));
    }
}
