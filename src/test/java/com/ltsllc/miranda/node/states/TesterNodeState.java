package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

public class TesterNodeState extends TestCase {
    @Mock
    private Node mockNode;

    public Node getMockNode() {
        return mockNode;
    }

    public void reset () {
        super.reset();

        mockNode = null;
    }

    public void setup () {
        super.setup();

        mockNode = mock(Node.class);
    }
}
