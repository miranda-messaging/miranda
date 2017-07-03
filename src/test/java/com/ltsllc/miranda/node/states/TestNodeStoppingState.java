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

package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.Results;
import com.ltsllc.miranda.network.messages.CloseResponseMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestNodeStoppingState extends TesterNodeState {
    private NodeStoppingState nodeStoppingState;

    public NodeStoppingState getNodeStoppingState() {
        return nodeStoppingState;
    }

    public void reset () {
        super.reset();

        nodeStoppingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setupMockMiranda();
        nodeStoppingState = new NodeStoppingState(getMockNode());
    }

    @Test
    public void testProcessDisconnectedMessage () {
        setupMockCluster();
        CloseResponseMessage closeResponseMessage = new CloseResponseMessage(null, this, 13,
                Results.Success);

        when(getMockNode().getCluster()).thenReturn(getMockCluster());

        State nextState = getNodeStoppingState().processMessage(closeResponseMessage);

        assert (nextState instanceof StopState);

        verify(getMockCluster(), atLeastOnce()).sendShutdownResponse(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyString());
    }
}
