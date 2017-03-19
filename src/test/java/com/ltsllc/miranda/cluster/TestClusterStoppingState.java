package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.messages.NewNodeMessage;
import com.ltsllc.miranda.cluster.states.ClusterStoppingState;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.messages.NodeStoppedMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/19/2017.
 */
public class TestClusterStoppingState extends TestCase {
    private ClusterStoppingState clusterStoppingState;

    public ClusterStoppingState getClusterStoppingState() {
        return clusterStoppingState;
    }

    public void reset () {
        super.reset();

        clusterStoppingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        clusterStoppingState = new ClusterStoppingState(getMockCluster());
    }

    @Test
    public void testProcessNodeStoppedMessage () {
        NodeElement nodeElement = new NodeElement("foo.com", "198.162.1.1", 6789, "a node");
        Node node = new Node(nodeElement, getMockNetwork(), getMockCluster());
        NodeStoppedMessage nodeStoppedMessage = new NodeStoppedMessage(null, this, node);
        List<Node> list = new ArrayList<Node>();
        list.add(node);

        when(getMockCluster().getNodes()).thenReturn(list);

        State nextState = getClusterStoppingState().processMessage(nodeStoppedMessage);

        assert (nextState == StopState.getInstance());
    }

    @Test
    public void testDicardMessage () {
        NewNodeMessage newNodeMessage = new NewNodeMessage(null, this, null);

        State nextState = getClusterStoppingState().processMessage(newNodeMessage);

        assert (nextState == getClusterStoppingState());
    }
}
