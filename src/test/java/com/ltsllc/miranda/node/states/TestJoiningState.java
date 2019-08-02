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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.messages.GetVersionsMessage;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.networkMessages.GetFileWireMessage;
import com.ltsllc.miranda.node.networkMessages.GetVersionsWireMessage;
import com.ltsllc.miranda.node.networkMessages.JoinResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/22/2017.
 */
public class TestJoiningState extends TesterNodeState {
    private JoiningState joiningState;

    public JoiningState getJoiningState() {
        return joiningState;
    }

    public void reset () throws Exception {
        super.reset();

        joiningState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        joiningState = new JoiningState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessJoinResponseSuccess () throws MirandaException {
        JoinResponseWireMessage joinResponseWireMessage = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Success);
        NetworkMessage networkMessage = new NetworkMessage(null, this, joinResponseWireMessage);

        State nextState = getJoiningState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
    }

    @Test
    public void testProcessJoinResponseFailure () throws MirandaException {
        JoinResponseWireMessage joinResponseWireMessage = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Failure);
        NetworkMessage networkMessage = new NetworkMessage(null, this, joinResponseWireMessage);

        State nextState = getJoiningState().processMessage(networkMessage);

        verify (getMockNetwork(), atLeastOnce()).sendClose(Matchers.any(), Matchers.any(), Matchers.anyInt());
        assert (nextState instanceof NodeStoppingState);
    }

    @Test
    public void testProcessGetVersions () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockMiranda();
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        NetworkMessage networkMessage = new NetworkMessage(null, this, getVersionsWireMessage);

        when(getMockMiranda().getQueue()).thenReturn(queue);

        getJoiningState().processMessage(networkMessage);

        assert (contains(Message.Subjects.GetVersions, queue));
    }

    /**
     * Someone wants to send the {@link com.ltsllc.miranda.node.networkMessages.GetFileWireMessage}
     */
    @Test
    public void testProcessGetClusterFile () throws MirandaException {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage(Files.Topic);
        GetClusterFileMessage getClusterFileMessage = new GetClusterFileMessage(null, this);

        when(getMockNode().getHandle()).thenReturn(13);

        getJoiningState().processMessage(getClusterFileMessage);

    }

    /**
     * Someone wants all the versions of the remote node.
     */
    @Test
    public void testProcessGetVersionsMessage () throws MirandaException {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        GetVersionsMessage getVersionsMessage = new GetVersionsMessage(null, this);

        when(getMockNode().getHandle()).thenReturn(13);

        getJoiningState().processMessage(getVersionsMessage);


    }
}
