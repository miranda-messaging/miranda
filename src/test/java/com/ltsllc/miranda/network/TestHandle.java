package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.mina.MinaHandle;
import com.ltsllc.miranda.mina.MinaHandler;
import com.ltsllc.miranda.node.networkMessages.ClusterFileWireMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.test.TestCase;
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

    public void reset () {
        super.reset();

        queue = null;
        mockMinaHandler = null;
        handle = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        queue = new LinkedBlockingQueue<Message>();
        mockMinaHandler = mock(MinaHandler.class);
        handle = new MinaHandle(mockMinaHandler, queue);
    }

    @Test
    public void testDeliver () {
        WireMessage testWireMessage = new ClusterFileWireMessage("whatever".getBytes(), new Version());

        getHandle().deliver(testWireMessage);

        assert (contains(Message.Subjects.NetworkMessage, getQueue()));
    }
}
