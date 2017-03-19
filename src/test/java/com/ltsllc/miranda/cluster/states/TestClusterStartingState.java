package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.LoadResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/19/2017.
 */
public class TestClusterStartingState extends TestCase {
    private ClusterStartingState clusterStartingState;

    public ClusterStartingState getClusterStartingState() {
        return clusterStartingState;
    }

    public void reset () {
        super.reset();

        clusterStartingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        clusterStartingState = new ClusterStartingState(getMockCluster());
    }

    @Test
    public void testProcessLoadResponse () {
        List<NodeElement> emptyList = new ArrayList<NodeElement>();

        LoadResponseMessage loadResponseMessage = new LoadResponseMessage(null, this, emptyList);

        getClusterStartingState().processMessage(loadResponseMessage);

        verify(getMockCluster(), atLeastOnce()).replaceNodes(Matchers.anyList());
    }

    @Test
    public void testProcessConnectMessage () {
        ConnectMessage connectMessage = new ConnectMessage(null, this);

        State nextState = getClusterStartingState().processMessage(connectMessage);

        assert (nextState instanceof ClusterReadyState);
        verify (getMockCluster(), atLeastOnce()).connect();
    }
}
