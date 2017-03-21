package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 3/21/2017.
 */
public class TestNodeState extends TesterNodeState {
    private NodeState nodeState;

    public NodeState getNodeState() {
        return nodeState;
    }

    public void reset () {
        super.reset();

        nodeState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        nodeState = new NodeState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessNetworkMessage () {
        JoinWireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        NetworkMessage networkMessage = new NetworkMessage(null, this, joinWireMessage);

        getNodeState().processMessage(networkMessage);
    }
}
