package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.StopResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.StopWireMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/22/2017.
 */
public class TestNodeDisconnectingState extends TesterNodeState {
    private NodeDisconnectingState disconnecting;

    public NodeDisconnectingState getDisconnecting() {
        return disconnecting;
    }

    public void reset () {
        super.reset();

        disconnecting = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        disconnecting = new NodeDisconnectingState(getMockNode());
    }

    @Test
    public void testProcessStopResponseWireMessage () {
        StopResponseWireMessage stopResponseWireMessage = new StopResponseWireMessage();
        NetworkMessage networkMessage = new NetworkMessage(null, this, stopResponseWireMessage);

        when(getMockNode().getNetwork()).thenReturn(getMockNetwork());

        State nextState = getDisconnecting().processMessage(networkMessage);

        verify(getMockNetwork(), atLeastOnce()).sendCloseMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyInt());
        assert (nextState instanceof NodeStoppingState);
    }

    @Test
    public void testProcessStopWireMessage () {
        StopWireMessage stopWireMessage = new StopWireMessage();
        NetworkMessage networkMessage = new NetworkMessage(null, this, stopWireMessage);

        when(getMockNode().getHandle()).thenReturn(13);
        when(getMockNode().getNetwork()).thenReturn(getMockNetwork());

        State nextState = getDisconnecting().processMessage(networkMessage);

        assert (nextState instanceof NodeDisconnectingState);
        StopResponseWireMessage stopResponseWireMessage = new StopResponseWireMessage();
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(13), Matchers.eq(stopResponseWireMessage));
    }
}
