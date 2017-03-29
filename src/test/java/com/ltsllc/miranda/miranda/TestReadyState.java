package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created by Clark on 3/5/2017.
 */
public class TestReadyState extends TestCase {
    private ReadyState readyState;

    public ReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        this.readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        this.readyState = new ReadyState(getMockMiranda());
    }

    @Test
    public void testProcessNewConnectionMessage () {
        Node node = new Node(-1, getMockNetwork(), getMockCluster());
        NewConnectionMessage message = new NewConnectionMessage(null, this, node);

        State nextState = getReadyState().processMessage(message);

        assert (nextState instanceof ReadyState);
        assert (contains(Message.Subjects.GetVersion, node.getQueue()));
    }
}
