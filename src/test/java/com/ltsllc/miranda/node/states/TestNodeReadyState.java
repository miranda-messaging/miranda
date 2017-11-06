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

import com.ltsllc.clcl.JavaKeyStore;
import com.ltsllc.clcl.PublicKey;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.cluster.networkMessages.DeleteUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.NewUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.UpdateUserWireMessage;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.messages.GetSubscriptionsFileMessage;
import com.ltsllc.miranda.node.messages.GetTopicsFileMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.node.networkMessages.*;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.user.messages.GetUsersFileMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestNodeReadyState extends TesterNodeState {
    private NodeReadyState readyState;

    public NodeReadyState getReadyState() {
        return readyState;
    }

    public void reset() throws MirandaException {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup() throws MirandaException {
        reset();

        super.setup();

        readyState = new NodeReadyState(getMockNode(), getMockNetwork());
    }

    @Test
    public void testProcessVersionMessage() throws MirandaException {
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


    public void testGetFileMessage(String file) throws MirandaException {
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
    public void testGetClusterFile() throws MirandaException {
        testGetFileMessage(Cluster.NAME);
    }

    @Test
    public void testGetUsersFile() throws MirandaException {
        testGetFileMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessVersionsMessage() throws MirandaException {
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
    public void testProcessGetFileResonseMessage() throws MirandaException {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(null, this, "whatever", "whtever");
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage("whatever", "whatever");

        State nextState = getReadyState().processMessage(getFileResponseMessage);

        assert (nextState instanceof NodeReadyState);
        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyInt(), Matchers.eq(getFileResponseWireMessage));
    }

    @Test
    public void testGetVersionsWireMessage() throws MirandaException {
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
    public void testProcessVersionsWireMessage() throws MirandaException {
        setupMockMiranda();
        setupMockCluster();
        Version version = new Version();
        NameVersion nameVersion = new NameVersion("whatever", version);
        List<NameVersion> nameVersionList = new ArrayList<NameVersion>();
        nameVersionList.add(nameVersion);
        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(nameVersionList);
        NetworkMessage networkMessage = new NetworkMessage(null, this, versionsWireMessage);

        BlockingQueue<Message> queue = new LinkedBlockingDeque<Message>();
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when(getMockCluster().getQueue()).thenReturn(queue);

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState instanceof NodeReadyState);
        assert (contains(Message.Subjects.Versions, queue));
    }

    public void testProcessGetFileWireMessage(String file) throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockMiranda();


        if (file.equalsIgnoreCase(Cluster.NAME)) {
            setupMockCluster();
            when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
            when(getMockCluster().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(UsersFile.FILE_NAME)) {
            setupMockUsersFile();
            when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
            when(getMockUserManager().getFile()).thenReturn(getMockUsersFile());
            when(getMockUsersFile().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(TopicsFile.FILE_NAME)) {
            setupMockTopicsFile();
            when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
            when(getMockTopicManager().getFile()).thenReturn(getMockTopicsFile());
            when(getMockTopicsFile().getQueue()).thenReturn(queue);
        } else if (file.equalsIgnoreCase(SubscriptionsFile.FILE_NAME)) {
            setupMockSubscriptionsFile();
            when(getMockMiranda().getSubscriptionManager()).thenReturn(getMockSubscriptionManager());
            when(getMockSubscriptionManager().getFile()).thenReturn(getMockSubscriptionsFile());
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
    public void testProcessGetFileWireMessageCluster() throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setupMockMiranda();
        setupMockCluster();

        when (getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when (getMockCluster().getQueue()).thenReturn(queue);

        testProcessGetFileWireMessage(Cluster.NAME);
    }

    @Test
    public void testProcessGetFileWireMessageUsers() throws MirandaException {
        testProcessGetFileWireMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageTopics() throws MirandaException {
        testProcessGetFileWireMessage(TopicsFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageSubscriptions() throws MirandaException {
        testProcessGetFileWireMessage(SubscriptionsFile.FILE_NAME);
    }

    @Test
    public void testProcessNewSessionWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY_PEM);
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

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY_PEM);
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


    public static final String TEST_PUBLIC_KEY_PEM =
                    "-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyLhCLtpp7TO6Z+mLHBn5\n" +
                    "3OnrWH8t/OpLCN1bLuu9B6VJ0005u2w0j5Ex3FsBL4zqRsVcFOqCu/e8GJnLWDhg\n" +
                    "101AsJf0y50jdlLBihfyspeeRfBFiEoudCAhR+ns8Y90tP8AVhkee0aC6WsAGfGG\n" +
                    "Q9MR68uQ6+qtmmg2LPU0VDTAx3UOHTuD5W5uWOiL15gCpeI86SRyB9mKavfHSMgS\n" +
                    "JWW14E5T+3s7X7FOiruF1SIHLwizygwVN4BFycGEx/As2qTYKU72vD++v/spH449\n" +
                    "uVHV8je0qHO3t60yKOVtTwnZVXx5/JS+uU63Ix0L0CejEHrqlcb7m94uJW2ysztz\n" +
                    "/QIDAQAB\n" +
                    "-----END PUBLIC KEY-----\n";


    @Test
    public void testProcessNewUserWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY_PEM);
        User user = userObject.asUser();
        NewUserWireMessage newUserWireMessage = new NewUserWireMessage(userObject);
        NetworkMessage networkMessage = new NetworkMessage(null, this, newUserWireMessage);

        when(getMockNode().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState == getReadyState());
        verify(getMockMiranda(), atLeastOnce()).sendUserAddedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.any(User.class));
    }

    @Test
    public void testProcessUpdateUserWireMessage() throws Exception {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY_PEM);
        User user = userObject.asUser();
        UpdateUserWireMessage updateUserWireMessage = new UpdateUserWireMessage(userObject);
        NetworkMessage networkMessage = new NetworkMessage(null, this, updateUserWireMessage);

        when(getMockNode().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(networkMessage);

        assert (nextState == getReadyState());
        verify(getMockMiranda(), atLeastOnce()).sendUserUpdatedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.any(User.class));
    }

    @Test
    public void testProcessDeleteUserWireMessage() throws MirandaException {
        setupMockMiranda();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY_PEM);
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
