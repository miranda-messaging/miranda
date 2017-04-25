package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 4/24/2017.
 */
public class TestClusterStartState extends TestCase {
    private ClusterStartState clusterStartState;

    @Mock
    private NodeElement mockNodeElement;

    public ClusterStartState getClusterStartState() {
        return clusterStartState;
    }

    public void reset () {
        super.reset();

        clusterStartState = null;
        mockNodeElement = null;
    }

    public NodeElement getMockNodeElement() {
        return mockNodeElement;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        clusterStartState = new ClusterStartState(getMockCluster());
        mockNodeElement = mock(NodeElement.class);

    }

    @Test
    public void testProcessFileLoadedMessage () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        nodeElementList.add(nodeElement);
        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this, nodeElementList);

        when(getMockCluster().getCurrentState()).thenReturn(getClusterStartState());
        when(getMockCluster().getNetwork()).thenReturn(getMockNetwork());

        State nextState = getClusterStartState().processMessage(fileLoadedMessage);

        assert (nextState instanceof ClusterReadyState);
        verify(getMockCluster(), atLeastOnce()).setNodes(Matchers.anyList());
        verify(getMockNetwork(), atLeastOnce()).sendConnect(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq("foo.com"), Matchers.eq(6789));
    }
}
