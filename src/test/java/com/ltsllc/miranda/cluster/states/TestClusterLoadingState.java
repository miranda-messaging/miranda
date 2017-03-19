package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.LoadResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/19/2017.
 */
public class TestClusterLoadingState extends TestCase {
    private ClusterLoadingState clusterLoadingState;

    public ClusterLoadingState getClusterLoadingState() {
        return clusterLoadingState;
    }

    public void reset () {
        super.reset();

        clusterLoadingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        clusterLoadingState = new ClusterLoadingState(getMockCluster());
    }

    @Test
    public void testProcessLoadResponse () {
        List<NodeElement> emptyList = new ArrayList<NodeElement>();
        LoadResponseMessage loadResponseMessage = new LoadResponseMessage(null, this, emptyList);

        State nextState = getClusterLoadingState().processMessage(loadResponseMessage);

        assert (nextState instanceof ClusterLoadingState);

        getClusterLoadingState().setDeferredConnect(true);

        nextState = getClusterLoadingState().processMessage(loadResponseMessage);

        verify(getMockCluster(), atLeastOnce()).connect();
        assert (nextState instanceof ClusterReadyState);
    }

    @Test
    public void testProcessConnectMessage () {
        ConnectMessage connectMessage = new ConnectMessage(null, this);

        State nextState = getClusterLoadingState().processMessage(connectMessage);

        assert (nextState instanceof ClusterLoadingState);
        assert (getClusterLoadingState().getDeferredConnect());

        getClusterLoadingState().setSeenLoad(true);

        nextState = getClusterLoadingState().processMessage(connectMessage);

        assert (nextState instanceof ClusterReadyState);
    }
}
