/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.NewNodeMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/19/2017.
 */
public class TestClusterStoppingState extends TestCase {
    private ClusterStoppingState clusterStoppingState;

    public ClusterStoppingState getClusterStoppingState() {
        return clusterStoppingState;
    }

    public void reset () throws Exception {
        super.reset();

        clusterStoppingState = null;
    }

    @Before
    public void setup () throws Exception {
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
