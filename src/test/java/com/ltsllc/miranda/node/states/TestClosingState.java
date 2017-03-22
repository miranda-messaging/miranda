package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.network.messages.CloseMessage;
import com.ltsllc.miranda.network.messages.ClosedMessage;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 3/21/2017.
 */
public class TestClosingState extends TesterNodeState {
    private ClosingState closingState;

    public ClosingState getClosingState() {
        return closingState;
    }

    public void reset () {
        super.reset();

        closingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        closingState = new ClosingState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessClosedMessage () {
        ClosedMessage closedMessage = new ClosedMessage(null, this, -1);

        State nextState = getClosingState().processMessage(closedMessage);

        assert (nextState instanceof StopState);
    }
}
