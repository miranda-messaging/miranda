package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestNodeStartState extends TesterNodeState {
    private NodeStartState nodeStartState;

    public NodeStartState getNodeStartState() {
        return nodeStartState;
    }

    public void reset () {
        super.reset();

        nodeStartState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        nodeStartState = new NodeStartState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessConnectMessage () {
        ConnectMessage connectMessage = new ConnectMessage(null, this);

        State nextState = getNodeStartState().processMessage(connectMessage);

        assert (nextState instanceof ConnectingState);
        verify(getMockNode(), atLeastOnce()).connect();
    }
}
