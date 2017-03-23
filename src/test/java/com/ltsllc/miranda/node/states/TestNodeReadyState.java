package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.GetFileResponseMessage;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.miranda.GetVersionsMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.*;
import com.ltsllc.miranda.node.networkMessages.*;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.GetUsersFileMessage;
import com.ltsllc.miranda.user.UsersFile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestNodeReadyState extends TesterNodeState {
    private NodeReadyState readyState;

    public NodeReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        readyState = new NodeReadyState(getMockNode(), getMockNetwork());
    }
/*
    @Test
    public void testTestGetVersions () {
        BlockingQueue<Message> queue = new LinkedBlockingDeque<Message>();
        setupMockMiranda();
        GetVersionsMessage getVersionsMessage = new GetVersionsMessage(null, this);
        when(getMockMiranda().getQueue()).thenReturn(queue);

        State nextState = getReadyState().processMessage(getVersionsMessage);

        assert (contains(Message.Subjects.GetVersions, queue));
        assert (nextState instanceof NodeReadyState);
    }
*/

    @Test
    public void testProcessVersionMessage () {
        Version version = new Version ();
        NameVersion nameVersion = new NameVersion("whatever", version);
        List<NameVersion> nameVersions = new ArrayList<NameVersion>();
        nameVersions.add(nameVersion);
        VersionMessage versionMessage = new VersionMessage(null, this, nameVersion);

        State nextState = getReadyState().processMessage(versionMessage);

        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(nameVersions);
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(versionsWireMessage));
    }


    public void testGetFileMessage (String file) {
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

        State nextState = getReadyState().processMessage(message);

        WireMessage wireMessage = new GetFileWireMessage(file);

        if (file.equalsIgnoreCase(SystemMessages.FILE_NAME))
            wireMessage = new GetMessagesWireMessage("whatever");
        else if (file.equalsIgnoreCase(SystemDeliveriesFile.FILE_NAME))
            wireMessage = new GetDeliveriesWireMessage("whatever");

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(wireMessage));

        assert (nextState instanceof NodeReadyState);
    }

    @Test
    public void testGetClusterFile () {
        testGetFileMessage(Cluster.FILE_NAME);
    }

    @Test
    public void testGetUsersFile () {
        testGetFileMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessVersionsMessage () {
        Version version = new Version();
        NameVersion nameVersion = new NameVersion("whatever", version);
        List<NameVersion> nameVersionList = new ArrayList<NameVersion>();
        nameVersionList.add(nameVersion);
        VersionsMessage versionsMessage = new VersionsMessage(null, this, nameVersionList);

        State nextState = getReadyState().processMessage(versionsMessage);

        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(nameVersionList);
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(versionsWireMessage));

        assert (nextState instanceof NodeReadyState);
    }

    @Test
    public void testProcessGetFileResonseMessage () {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(null, this, "whatever", "whtever");
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage("whatever", "whatever");

        State nextState = getReadyState().processMessage(getFileResponseMessage);

        assert (nextState instanceof NodeReadyState);
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(getFileResponseWireMessage));
    }

    @Test
    public void testGetVersionsWireMessage () {
        setupMockMiranda();
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        NetworkMessage networkMessage = new NetworkMessage(null, this, getVersionsWireMessage);

        BlockingQueue<Message> queue = new LinkedBlockingDeque<Message>();
        when(getMockMiranda().getQueue()).thenReturn(queue);
        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
        assert (contains(Message.Subjects.GetVersions, queue));
    }

    @Test
    public void testProcessVersionsWireMessage () {
        setupMockCluster();
        Version version = new Version();
        NameVersion nameVersion = new NameVersion("whatever", version);
        List<NameVersion> nameVersionList = new ArrayList<NameVersion>();
        nameVersionList.add(nameVersion);
        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(nameVersionList);
        NetworkMessage networkMessage = new NetworkMessage(null, this, versionsWireMessage);

        BlockingQueue<Message> queue = new LinkedBlockingDeque<Message>();
        when(getMockCluster().getQueue()).thenReturn(queue);

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
        assert (contains(Message.Subjects.Versions, queue));
    }

    public void testProcessGetFileWireMessage (String file) {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        if (file.equalsIgnoreCase(Cluster.FILE_NAME)) {
            setupMockCluster();
            when(getMockCluster().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(UsersFile.FILE_NAME)) {
            setupMockUsersFile();
            when(getMockUsersFile().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(TopicsFile.FILE_NAME)) {
            setupMockTopicsFile();
            when(getMockTopicsFile().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(SubscriptionsFile.FILE_NAME)) {
            setupMockSubscriptionsFile();
            when (getMockSubscriptionsFile().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(SystemMessages.FILE_NAME)) {
            setupMockSystemMessages();
            when (getMockSystemMessages().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(SystemDeliveriesFile.FILE_NAME)) {
            setupMockSystemDeliveries();
            when (getMockSystemDeliveriesFile().getQueue()).thenReturn(queue);
        } else {
            System.err.println ("Unrecognized file: " + file);
            System.exit(1);
        }

        WireMessage wireMessage = new GetFileWireMessage(file);

        if (file.equalsIgnoreCase(SystemMessages.FILE_NAME))
            wireMessage = new GetMessagesWireMessage("whatever");
        else if (file.equalsIgnoreCase(SystemDeliveriesFile.FILE_NAME))
            wireMessage = new GetDeliveriesWireMessage("whatever");

        NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);

        if (file.equalsIgnoreCase(SystemMessages.FILE_NAME)) {
            verify (getMockSystemMessages(), atLeastOnce()).sendGetSystemMessages(Matchers.any(BlockingQueue.class),
                    Matchers.any(), Matchers.eq("whatever"));
        } else if (file.equalsIgnoreCase(SystemDeliveriesFile.FILE_NAME)) {
            verify (getMockSystemDeliveriesFile(), atLeastOnce()).sendGetSystemDeliveries(Matchers.any(BlockingQueue.class),
                    Matchers.any(), Matchers.eq("whatever"));
        } else {
            assert (contains(Message.Subjects.GetFile, queue));
        }
    }

    @Test
    public void testProcessGetFileWireMessageCluster () {
        testProcessGetFileWireMessage(Cluster.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageUsers () {
        testProcessGetFileWireMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageTopics () {
        testProcessGetFileWireMessage(TopicsFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageSubscriptions () {
        testProcessGetFileWireMessage(SubscriptionsFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageSystemMessages () {
        testProcessGetFileWireMessage(SystemMessages.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageSystemDeliveries () {
        testProcessGetFileWireMessage(SystemDeliveriesFile.FILE_NAME);
    }
}
