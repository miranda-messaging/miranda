package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
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

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/22/2017.
 */
public class TestJoiningState extends TesterNodeState {
    private JoiningState joiningState;

    public JoiningState getJoiningState() {
        return joiningState;
    }

    public void reset () {
        super.reset();

        joiningState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        joiningState = new JoiningState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessJoinResponseSuccess () {
        JoinResponseWireMessage joinResponseWireMessage = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Success);
        NetworkMessage networkMessage = new NetworkMessage(null, this, joinResponseWireMessage);

        State nextState = getJoiningState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
    }

    @Test
    public void testProcessJoinResponseFailure () {
        JoinResponseWireMessage joinResponseWireMessage = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Failure);
        NetworkMessage networkMessage = new NetworkMessage(null, this, joinResponseWireMessage);

        State nextState = getJoiningState().processMessage(networkMessage);

        verify (getMockNetwork(), atLeastOnce()).sendClose(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyInt());
        assert (nextState instanceof NodeStoppingState);
    }

    @Test
    public void testProcessGetVersions () {
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
    public void testProcessGetClusterFile () {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage(Cluster.NAME);
        GetClusterFileMessage getClusterFileMessage = new GetClusterFileMessage(null, this);

        when(getMockNode().getHandle()).thenReturn(13);

        getJoiningState().processMessage(getClusterFileMessage);

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(13), Matchers.eq(getFileWireMessage));
    }

    /**
     * Someone wants all the versions of the remote node.
     */
    @Test
    public void testProcessGetVersionsMessage () {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        GetVersionsMessage getVersionsMessage = new GetVersionsMessage(null, this);

        when(getMockNode().getHandle()).thenReturn(13);

        getJoiningState().processMessage(getVersionsMessage);

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(13), Matchers.eq(getVersionsWireMessage));
    }
}
