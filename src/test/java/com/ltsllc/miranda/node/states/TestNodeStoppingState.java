package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.network.messages.DisconnectedMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestNodeStoppingState extends TesterNodeState {
    private NodeStoppingState nodeStoppingState;

    public NodeStoppingState getNodeStoppingState() {
        return nodeStoppingState;
    }

    public void reset () {
        super.reset();

        nodeStoppingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        nodeStoppingState = new NodeStoppingState(getMockNode());
    }

    @Test
    public void testProcessDiconnectedMessage () {
        setupMockCluster();
        DisconnectedMessage disconnectedMessage = new DisconnectedMessage(null, this, 13);

        when(getMockNode().getCluster()).thenReturn(getMockCluster());

        State nextState = getNodeStoppingState().processMessage(disconnectedMessage);

        assert (nextState instanceof StopState);

        verify(getMockCluster(), atLeastOnce()).sendNodeStopped(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(getMockNode()));
    }
}
