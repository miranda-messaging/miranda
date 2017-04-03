package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.*;
import com.ltsllc.miranda.node.networkMessages.*;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.messages.GetUsersFileMessage;
import com.ltsllc.miranda.user.UsersFile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/22/2017.
 */
public class TestNewNodeState extends TesterNodeState {
    private NewNodeState newNodeState;

    public NewNodeState getNewNodeState() {
        return newNodeState;
    }

    public void reset () {
        super.reset();

        newNodeState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        newNodeState = new NewNodeState(getMockNode(), getMockNetwork(), getMockCluster());
    }

    @Test
    public void testProcessJoinWireMessage () {
        JoinResponseWireMessage joinResponseWireMessage = new JoinResponseWireMessage(JoinResponseWireMessage.Responses.Success);
        JoinWireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        NetworkMessage networkMessage = new NetworkMessage(null, this, joinWireMessage);

        State nextState = getNewNodeState().processMessage(networkMessage);

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(joinResponseWireMessage));

        assert (nextState instanceof NodeReadyState);
    }

    @Test
    public void testProcessVersionsWireMessage () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockMiranda();

        List<NameVersion> nameVersions = new ArrayList<NameVersion>();
        Version version = new Version();
        version.setSha1("whatever");
        nameVersions.add (new NameVersion(Cluster.FILE_NAME, version));
        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(nameVersions);
        NetworkMessage networkMessage = new NetworkMessage(null, this, versionsWireMessage);

        when(getMockMiranda().getQueue()).thenReturn(queue);

        State nextState = getNewNodeState().processMessage(networkMessage);

        assert (nextState instanceof NewNodeState);
        assert (contains(Message.Subjects.Versions, queue));
    }

    @Test
    public void testGetFileResponseWireMessageCluster () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockCluster();
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(Cluster.FILE_NAME, "whatever");
        NetworkMessage networkMessage = new NetworkMessage(null, this, getFileResponseWireMessage);

        when (getMockCluster().getQueue()).thenReturn(queue);

        State nextState = getNewNodeState().processMessage(networkMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
        assert (nextState instanceof NewNodeState);
    }

    @Test
    public void testGetFileResponseWireMessageUsers () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockUsersFile();
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(UsersFile.FILE_NAME, "whatever");
        NetworkMessage networkMessage = new NetworkMessage(null, this, getFileResponseWireMessage);

        when (getMockUsersFile().getQueue()).thenReturn(queue);

        State nextState = getNewNodeState().processMessage(networkMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
        assert (nextState instanceof NewNodeState);
    }

    @Test
    public void testGetFileResponseWireMessageTopics () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockTopicsFile();
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(TopicsFile.FILE_NAME, "whatever");
        NetworkMessage networkMessage = new NetworkMessage(null, this, getFileResponseWireMessage);

        when (getMockTopicsFile().getQueue()).thenReturn(queue);

        State nextState = getNewNodeState().processMessage(networkMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
        assert (nextState instanceof NewNodeState);
    }

    @Test
    public void testGetFileResponseWireMessageSubscriptions () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockSubscriptionsFile();
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(SubscriptionsFile.FILE_NAME, "whatever");
        NetworkMessage networkMessage = new NetworkMessage(null, this, getFileResponseWireMessage);

        when (getMockSubscriptionsFile().getQueue()).thenReturn(queue);

        State nextState = getNewNodeState().processMessage(networkMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
        assert (nextState instanceof NewNodeState);
    }

    @Test
    public void testGetFileResponseWireMessageSystemMessages () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockSystemMessages();
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(SystemMessages.FILE_NAME, "whatever");
        NetworkMessage networkMessage = new NetworkMessage(null, this, getFileResponseWireMessage);

        when (getMockSystemMessages().getQueue()).thenReturn(queue);

        State nextState = getNewNodeState().processMessage(networkMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
        assert (nextState instanceof NewNodeState);
    }

    @Test
    public void testGetFileResponseWireMessageSystemDeliveries () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockSystemDeliveries();
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(SystemDeliveriesFile.FILE_NAME, "whatever");
        NetworkMessage networkMessage = new NetworkMessage(null, this, getFileResponseWireMessage);

        when (getMockSystemDeliveriesFile().getQueue()).thenReturn(queue);

        State nextState = getNewNodeState().processMessage(networkMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
        assert (nextState instanceof NewNodeState);
    }

    public void testProcessGetFileMessage (String file) {
        Message message = null;

        if (file.equalsIgnoreCase(UsersFile.FILE_NAME))
            message = new GetUsersFileMessage(null, this);
        else if (file.equalsIgnoreCase(TopicsFile.FILE_NAME))
            message = new GetTopicsFileMessage(null, this);
        else if (file.equalsIgnoreCase(SubscriptionsFile.FILE_NAME))
            message = new GetSubscriptionsFileMessage(null, this);
        else if (file.equalsIgnoreCase(SystemMessages.FILE_NAME))
            message = new GetSystemMessagesMessage(null, this, "whatever");
        else if (file.equalsIgnoreCase(SystemDeliveriesFile.FILE_NAME))
            message = new GetDeliveriesMessage(null, this, "whatever");
        else if (file.equalsIgnoreCase(Cluster.FILE_NAME))
            message = new GetClusterFileMessage(null, this);
        else {
            System.err.println ("Unrecognized file: " + file);
            System.exit(1);
        }

        State nextState = getNewNodeState().processMessage(message);

        WireMessage wireMessage = new GetFileWireMessage(file);

        if (file.equalsIgnoreCase(SystemMessages.FILE_NAME))
            wireMessage = new GetMessagesWireMessage("whatever");
        else if (file.equalsIgnoreCase(SystemDeliveriesFile.FILE_NAME))
            wireMessage = new GetDeliveriesWireMessage("whatever");

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(wireMessage));

        assert (nextState instanceof NewNodeState);
    }

    @Test
    public void testProcessGetUsersFileMessage () {
        testProcessGetFileMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessGetClusterFileMessage () {
        testProcessGetFileMessage(Cluster.FILE_NAME);
    }

    @Test
    public void testProcessGetTopicsFileMessage () {
        testProcessGetFileMessage(TopicsFile.FILE_NAME);
    }

    @Test
    public void testProcessGetSubscriptionsFileMessage () {
        testProcessGetFileMessage(SubscriptionsFile.FILE_NAME);
    }

    @Test
    public void testProcessGetSystemMessagesMessage () {
        testProcessGetFileMessage(SystemMessages.FILE_NAME);
    }

    @Test
    public void testProcessGetSystemDeliveriesMessage () {
        testProcessGetFileMessage(SystemDeliveriesFile.FILE_NAME);
    }

    @Test
    public void testProcessConnectMessage () {
        NewNodeState.setLogger(getMockLogger());
        ConnectMessage connectMessage = new ConnectMessage(null, this);

        when(getMockNode().getCurrentState()).thenReturn(getNewNodeState());

        State nextState = getNewNodeState().processMessage(connectMessage);

        assert (nextState == getNewNodeState());
        verify (getMockLogger(), atLeastOnce()).warn(Matchers.anyString(), Matchers.any(Throwable.class));
    }
}
