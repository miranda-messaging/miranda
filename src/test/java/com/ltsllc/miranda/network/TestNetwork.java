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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.messages.CloseMessage;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/20/2017.
 */
public class TestNetwork extends TestCase {
    @Mock
    private Handle mockHandle;

    private com.ltsllc.miranda.test.TestNetwork testNetwork;
    private int handleId;

    public com.ltsllc.miranda.test.TestNetwork getTestNetwork() {
        return testNetwork;
    }

    public Handle getMockHandle() {
        return mockHandle;
    }

    public int getHandleId() {
        return handleId;
    }

    public void setHandleId (int handleId) {
        this.handleId = handleId;
    }

    public void reset () {
        super.reset();

        handleId = -1;
        mockHandle = null;
        testNetwork = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockHandle = mock(Handle.class);
        testNetwork = new com.ltsllc.miranda.test.TestNetwork();
    }

    @After
    public void cleanup () {
        deleteFile("data/cluste.json");
    }

    public void setHandleId (BlockingQueue<Message> queue) {
        for (Message message : queue)
        {
            if (message.getSubject() == Message.Subjects.ConnectSucceeded) {
                ConnectSucceededMessage connectSucceededMessage = (ConnectSucceededMessage) message;
                setHandleId(connectSucceededMessage.getHandle());
            }
        }
    }

    @Test
    public void testConstructor () {
        assert (getTestNetwork().getCurrentState() instanceof NetworkReadyState);
    }

    @Test
    public void testConnectSuccess () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        getTestNetwork().setTestHandle(getMockHandle());

        ConnectToMessage connectToMessage = new ConnectToMessage("foo.com", 6789, queue, this);

        getTestNetwork().connect(connectToMessage);

        setHandleId(queue);

        assert (getTestNetwork().verifyCall());
        assert (contains(Message.Subjects.ConnectSucceeded, queue));
    }

    @Test
    public void testConnectNull () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        ConnectToMessage connectToMessage = new ConnectToMessage("foo.com", 6789, queue, this);

        getTestNetwork().connect(connectToMessage);

        assert (getTestNetwork().verifyCall());
        assert (contains(Message.Subjects.ConnectFailed, queue));
    }

    @Test
    public void testConnectException () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        NetworkException networkException = new NetworkException("Test", NetworkException.Errors.Test);
        ConnectToMessage connectToMessage = new ConnectToMessage("foo.com", 6789, queue, this);

        getTestNetwork().setBasicConnectException(networkException);

        getTestNetwork().connect(connectToMessage);

        assert (getTestNetwork().verifyCall());
        assert (contains(Message.Subjects.ConnectFailed, queue));
    }

    @Test
    public void testDisconnectSuccess () {
        testConnectSuccess();

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        CloseMessage closeMessage = new CloseMessage(queue, this, getHandleId());

        getTestNetwork().setTestHandle(getMockHandle());

        getTestNetwork().disconnect(closeMessage);

        verify(getMockHandle(), atLeastOnce()).close();
        assert (null == getTestNetwork().getHandle(getHandleId()));
        assert (contains(Message.Subjects.Disconnected, queue));
    }

    @Test
    public void testDisconnectHandleNotFound () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        CloseMessage closeMessage = new CloseMessage(queue, this, -1);

        getTestNetwork().disconnect(closeMessage);

        assert (null == getTestNetwork().getHandle(-1));
        assert (contains(Message.Subjects.UnknownHandle, queue));
    }

    @Test
    public void testForceDisconnect () {
        testConnectSuccess();

        getTestNetwork().forceDisconnect(getHandleId());

        assert (null == getTestNetwork().getHandle(getHandleId()));
    }
}
