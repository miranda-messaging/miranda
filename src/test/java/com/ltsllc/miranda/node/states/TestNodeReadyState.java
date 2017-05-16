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
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.cluster.networkMessages.DeleteUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.NewUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.UpdateUserWireMessage;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.*;
import com.ltsllc.miranda.node.networkMessages.*;
import com.ltsllc.miranda.servlet.user.UserObject;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.messages.GetUsersFileMessage;
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

    public void reset() {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup() {
        reset();

        super.setup();

        readyState = new NodeReadyState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessVersionMessage() {
        Version version = new Version();
        NameVersion nameVersion = new NameVersion("whatever", version);
        List<NameVersion> nameVersions = new ArrayList<NameVersion>();
        nameVersions.add(nameVersion);
        VersionMessage versionMessage = new VersionMessage(null, this, nameVersion);

        State nextState = getReadyState().processMessage(versionMessage);

        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(nameVersions);
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(versionsWireMessage));
    }


    public void testGetFileMessage(String file) {
        Message message = null;

        if (file.equalsIgnoreCase(UsersFile.FILE_NAME))
            message = new GetUsersFileMessage(null, this);
        else if (file.equalsIgnoreCase(TopicsFile.FILE_NAME))
            message = new GetTopicsFileMessage(null, this);
        else if (file.equalsIgnoreCase(SubscriptionsFile.FILE_NAME))
            message = new GetSubscriptionsFileMessage(null, this);
        else if (file.equalsIgnoreCase(Cluster.NAME))
            message = new GetClusterFileMessage(null, this);
        else {
            System.err.println("Unrecognized file: " + file);
            System.exit(1);
        }

        State nextState = getReadyState().processMessage(message);

        WireMessage wireMessage = new GetFileWireMessage(file);

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(wireMessage));

        assert (nextState instanceof NodeReadyState);
    }

    @Test
    public void testGetClusterFile() {
        testGetFileMessage(Cluster.NAME);
    }

    @Test
    public void testGetUsersFile() {
        testGetFileMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessVersionsMessage() {
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
    public void testProcessGetFileResonseMessage() {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(null, this, "whatever", "whtever");
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage("whatever", "whatever");

        State nextState = getReadyState().processMessage(getFileResponseMessage);

        assert (nextState instanceof NodeReadyState);
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(getFileResponseWireMessage));
    }

    @Test
    public void testGetVersionsWireMessage() {
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
    public void testProcessVersionsWireMessage() {
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

    public void testProcessGetFileWireMessage(String file) {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        if (file.equalsIgnoreCase(Cluster.NAME)) {
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
            when(getMockSubscriptionsFile().getQueue()).thenReturn(queue);
        } else {
            System.err.println("Unrecognized file: " + file);
            System.exit(1);
        }

        WireMessage wireMessage = new GetFileWireMessage(file);


        NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
        assert (contains(Message.Subjects.GetFile, queue));
    }

    @Test
    public void testProcessGetFileWireMessageCluster() {
        testProcessGetFileWireMessage(Cluster.NAME);
    }

    @Test
    public void testProcessGetFileWireMessageUsers() {
        testProcessGetFileWireMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageTopics() {
        testProcessGetFileWireMessage(TopicsFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageSubscriptions() {
        testProcessGetFileWireMessage(SubscriptionsFile.FILE_NAME);
    }

    @Test
    public void testProcessNewSessionWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        Session session = new Session(user, 123, 456);
        NewSessionWireMessage newSessionWireMessage = new NewSessionWireMessage(session);
        NetworkMessage networkMessage = new NetworkMessage(null, this, newSessionWireMessage);

        when(getMockNode().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
        verify(getMockMiranda(), atLeastOnce()).sendAddSessionMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(session));
    }

    @Test
    public void testProcessSessinsExpiredWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        Session session = new Session(user, 123, 456);
        List<Session> expiredSessions = new ArrayList<Session>();
        expiredSessions.add(session);
        SessionsExpiredWireMessage sessionsExpiredWireMessage = new SessionsExpiredWireMessage(expiredSessions);
        NetworkMessage networkMessage = new NetworkMessage(null, this, sessionsExpiredWireMessage);

        when(getMockNode().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
        verify(getMockMiranda(), atLeastOnce()).sendSessionsExpiredMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(expiredSessions));
    }


    public static final String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpjR9MH5cTEPIXR/0cLp/Lw3QDK4RMPIygL8Aqh0yQ/MOpQtXrBzwSph4N1NURg1tB3EuyCVGsTfSfrbR5nqsN5IiaJyBuvhThBLwHyKN+PEUQ/rB6qUyg+jcPigTfqj6gksNxnC6CmCJ6XpBOiBOORgFQvdISo7pOqxZKxmaTqwIDAQAB";

    @Test
    public void testProcessNewUserWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        NewUserWireMessage newUserWireMessage = new NewUserWireMessage(userObject);
        NetworkMessage networkMessage = new NetworkMessage(null, this, newUserWireMessage);

        when(getMockNode().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState == getReadyState());
        verify(getMockMiranda(), atLeastOnce()).sendUserAddedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(user));
    }

    @Test
    public void testProcessUpdateUserWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        UpdateUserWireMessage updateUserWireMessage = new UpdateUserWireMessage(userObject);
        NetworkMessage networkMessage = new NetworkMessage(null, this, updateUserWireMessage);

        when(getMockNode().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState == getReadyState());
        verify(getMockMiranda(), atLeastOnce()).sendUserUpdatedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(user));
    }

    @Test
    public void testProcessDeleteUserWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        DeleteUserWireMessage deleteUserWireMessage = new DeleteUserWireMessage("whatever");
        NetworkMessage networkMessage = new NetworkMessage(null, this, deleteUserWireMessage);

        when(getMockNode().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState == getReadyState());
        verify(getMockMiranda(), atLeastOnce()).sendUserDeletedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq("whatever"));
    }
}
