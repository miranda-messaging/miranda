package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.ClusterFileMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/19/2017.
 */
public class TestCusterConnectingState extends TestCase {
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

        clusterConnectingState = new ClusterConnectingState(getMockCluster());
    }

    @Test
    public void testProcessClusterFileMessage () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        List<NodeElement> list = new ArrayList<NodeElement>();
        list.add(nodeElement);
        ClusterFileMessage clusterFileMessage = new ClusterFileMessage(null, this, list, null);

        when (getMockCluster().getNetwork()).thenReturn(getMockNetwork());

        State nextState = getClusterConnectingState().processMessage(clusterFileMessage);

        assert (nextState instanceof ClusterReadyState);
    }
}
