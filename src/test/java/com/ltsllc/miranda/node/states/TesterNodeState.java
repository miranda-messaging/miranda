package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.test.TestCase;
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

        setuplog4j();

        mockNode = mock(Node.class);
    }
}
