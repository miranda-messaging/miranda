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
import com.ltsllc.miranda.node.networkMessages.JoinResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/22/2017.
 */
public class TestNodeIncomingStartState extends TesterNodeState {
    private NodeIncomingStartState nodeIncoming;

    public NodeIncomingStartState getNodeIncoming() {
        return nodeIncoming;
    }

    public void reset () {
        super.reset();

        nodeIncoming = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        nodeIncoming = new NodeIncomingStartState(getMockNode(), getMockNetwork(), getMockCluster());
    }

    @Test
    public void testProcessJoinWireMessage () {
        JoinWireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        NetworkMessage networkMessage = new NetworkMessage(null, this, joinWireMessage);

        when(getMockNode().getNetwork()).thenReturn(getMockNetwork());
        when(getMockNode().getHandle()).thenReturn(13);

        State nextState = getNodeIncoming().processMessage(networkMessage);

        verify(getMockNode(), atLeastOnce()).setDns(Matchers.eq("foo.com"));
        verify(getMockNode(), atLeastOnce()).setIp(Matchers.eq("192.168.1.1"));
        verify(getMockNode(), atLeastOnce()).setPort(Matchers.eq(6789));
        verify(getMockNode(), atLeastOnce()).setDescription("a node");

        assert (nextState instanceof NodeReadyState);
        JoinResponseWireMessage joinResponseWireMessage = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Success);

        verify (getMockNetwork(), atLeastOnce()).sendMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(joinResponseWireMessage));

        verify(getMockCluster(), atLeastOnce()).newNode(Matchers.eq(getMockNode()));
    }
}
