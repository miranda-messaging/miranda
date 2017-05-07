package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.NewNodeMessage;
import com.ltsllc.miranda.cluster.states.ClusterStoppingState;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.messages.NodeStoppedMessage;
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
    public void testProcessShutdownResponseMessageNodeIntermediate () {
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(null, this, "foo.com:6789");

        when(getMockCluster().disconnected()).thenReturn(false);
        when(getMockCluster().getClusterFileResponded()).thenReturn(true);
        when(getMockCluster().getCurrentState()).thenReturn(getClusterStoppingState());

        State nextState = getClusterStoppingState().processMessage(shutdownResponseMessage);

        assert (nextState == getClusterStoppingState());
    }

    @Test
    public void testProcessShutdownResponseMessageFileIntermediate () {
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(null, this, ClusterFile.NAME);

        when (getMockCluster().disconnected()).thenReturn(false);
        when (getMockCluster().getClusterFileResponded()).thenReturn(false);
        when (getMockCluster().getCurrentState()).thenReturn(getClusterStoppingState());

        State nextState = getClusterStoppingState().processMessage(shutdownResponseMessage);

        assert (nextState == getClusterStoppingState());
    }

    @Test
    public void testProcessShutdownResponseMessageNodeFinal () {
        setupMockMiranda();
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(null, this, "foo.com:6789");

        when(getMockCluster().disconnected()).thenReturn(true);
        when(getMockCluster().getClusterFileResponded()).thenReturn(true);

        State nextState = getClusterStoppingState().processMessage(shutdownResponseMessage);

        assert (nextState == StopState.getInstance());
        verify(getMockMiranda(), atLeastOnce()).sendShutdownResponse(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(Cluster.NAME));
    }

    @Test
    public void testProcessShutdownResponseMessageFileFinal () {
        setupMockMiranda();
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(null, this, "foo.com:6789");

        when(getMockCluster().disconnected()).thenReturn(true);
        when(getMockCluster().getClusterFileResponded()).thenReturn(true);

        State nextState = getClusterStoppingState().processMessage(shutdownResponseMessage);

        assert (nextState == StopState.getInstance());
        verify(getMockMiranda(), atLeastOnce()).sendShutdownResponse(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(Cluster.NAME));
    }

    @Test
    public void testDiscardMessage () {
        NewNodeMessage newNodeMessage = new NewNodeMessage(null, this, null);

        State nextState = getClusterStoppingState().processMessage(newNodeMessage);

        assert (nextState == getClusterStoppingState());
    }
}
