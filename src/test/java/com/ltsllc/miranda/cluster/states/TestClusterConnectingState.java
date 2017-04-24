package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.messages.ClusterFileMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/24/2017.
 */
public class TestClusterConnectingState extends TestCase {
    private ClusterConnectingState clusterConnectingState;

    public ClusterConnectingState getClusterConnectingState() {
        return clusterConnectingState;
    }

    public void reset () {
        super.reset();

        clusterConnectingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        clusterConnectingState = new ClusterConnectingState(getMockCluster());
    }

    @Test
    public void testProcessClusterFileMessage () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        nodeElementList.add(nodeElement);

        ClusterFileMessage clusterFileMessage = new ClusterFileMessage(null, this, nodeElementList,
                new Version());

        when(getMockCluster().getCurrentState()).thenReturn(getClusterConnectingState());
        when(getMockCluster().getNetwork()).thenReturn(getMockNetwork());

        State nextState = getClusterConnectingState().processMessage(clusterFileMessage);

        assert (nextState instanceof ClusterReadyState);
        verify (getMockCluster(), atLeastOnce()).setNodes(Matchers.anyList());
        verify (getMockNetwork(), atLeastOnce()).sendConnect(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq("foo.com"), Matchers.eq(6789));
    }
}
