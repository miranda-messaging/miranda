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
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.StopResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.StopWireMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/22/2017.
 */
public class TestNodeDisconnectingState extends TesterNodeState {
    private NodeDisconnectingState disconnecting;

    public NodeDisconnectingState getDisconnecting() {
        return disconnecting;
    }

    public void reset () throws Exception {
        super.reset();

        disconnecting = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        disconnecting = new NodeDisconnectingState(getMockNode());
    }

    @Test
    public void testProcessStopResponseWireMessage () throws MirandaException {
        StopResponseWireMessage stopResponseWireMessage = new StopResponseWireMessage();
        NetworkMessage networkMessage = new NetworkMessage(null, this, stopResponseWireMessage);

        when(getMockNode().getNetwork()).thenReturn(getMockNetwork());

        State nextState = getDisconnecting().processMessage(networkMessage);

        verify(getMockNetwork(), atLeastOnce()).sendCloseMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyInt());
        assert (nextState instanceof NodeStoppingState);
    }

    @Test
    public void testProcessStopWireMessage () throws MirandaException {
        StopWireMessage stopWireMessage = new StopWireMessage();
        NetworkMessage networkMessage = new NetworkMessage(null, this, stopWireMessage);

        when(getMockNode().getHandle()).thenReturn(13);
        when(getMockNode().getNetwork()).thenReturn(getMockNetwork());

        State nextState = getDisconnecting().processMessage(networkMessage);

        assert (nextState instanceof NodeDisconnectingState);
        StopResponseWireMessage stopResponseWireMessage = new StopResponseWireMessage();
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(13), Matchers.eq(stopResponseWireMessage));
    }
}
