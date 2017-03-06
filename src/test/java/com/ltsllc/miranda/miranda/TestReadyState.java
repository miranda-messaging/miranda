package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.NewConnectionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

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
        setuplog4j();
        String[] empty = new String[0];
        Miranda miranda = new Miranda(empty);
        miranda.start();

        pause(1000);

        this.readyState = (ReadyState) miranda.getCurrentState();
    }

    @Test
    public void testProcessNewConnectionMessage () {
        Node node = new Node(-1);
        NewConnectionMessage message = new NewConnectionMessage(null, this, node);
        send(message, getReadyState().getMiranda().getQueue());

        pause(250);

        assert (contains(Message.Subjects.GetVersion, node.getQueue()));
    }
}
