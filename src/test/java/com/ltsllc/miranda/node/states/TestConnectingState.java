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
import com.ltsllc.miranda.network.messages.ConnectFailedMessage;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/21/2017.
 */
public class TestConnectingState extends TesterNodeState {
    private ConnectingState connectingState;

    public ConnectingState getConnectingState() {
        return connectingState;
    }

    public void reset () {
        super.reset();

        connectingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        connectingState = new ConnectingState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessConnectSucceededMessage () {
        ConnectSucceededMessage connectSucceededMessage = new ConnectSucceededMessage(null, this, 13);

        when(getMockNode().getHandle()).thenReturn(13);

        State nextState = getConnectingState().processMessage(connectSucceededMessage);

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(13), Matchers.any(JoinWireMessage.class));
        assert (nextState instanceof JoiningState);
    }

    @Test
    public void testProcessConnectFailedMessage () {
        ConnectFailedMessage connectFailed = new ConnectFailedMessage(null, this, null);

        when(getMockNode().getDns()).thenReturn("foo.com");
        when(getMockNode().getPort()).thenReturn(6789);

        State nextState = getConnectingState().processMessage(connectFailed);

        assert (nextState instanceof RetryingState);
    }
}
