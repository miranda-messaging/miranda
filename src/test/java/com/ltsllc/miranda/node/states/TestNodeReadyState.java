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
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.cluster.networkMessages.DeleteUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.NewUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.UpdateUserWireMessage;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.*;
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
import java.util.regex.Matcher;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestNodeReadyState extends TesterNodeState {
    private NodeReadyState readyState;

    public NodeReadyState getReadyState() {
        return readyState;
    }

    public void reset() throws Exception {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup() throws Exception {
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
    }


    public void testGetFileMessage(String file) throws MirandaException {
        Message message = null;

        if (file.equalsIgnoreCase(UsersFile.FILE_NAME))
            message = new GetFileMessage(null, this, Files.User);
        else if (file.equalsIgnoreCase(TopicsFile.FILE_NAME))
            message = new GetFileMessage(null, this, Files.Topic);
        else if (file.equalsIgnoreCase(SubscriptionsFile.FILE_NAME))
            message = new GetFileMessage(null, this, Files.Subscription);
        else if (file.equalsIgnoreCase(Cluster.NAME))
            message = new GetFileMessage(null, this, Files.Cluster);
        else {
            System.err.println("Unrecognized file: " + file);
            System.exit(1);
        }

        State nextState = getReadyState().processMessage(message);

        WireMessage wireMessage = new GetFileWireMessage(Files.Topic);
        assert (wireMessage.getWireSubject() == WireMessage.WireSubjects.GetFile);
        assert (nextState instanceof NodeReadyState);
    }

    private byte[] data = {1,2,3};

    @Test
    public void testGetClusterFile() throws MirandaException {
        setupMockCluster();
        setupMockClutersfile();
        setupMockSingleFile();
        setupMockMiranda();

        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when(getMockCluster().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getBytes()).thenReturn(data);
        when(getMockSingleFile().getVersion()).thenReturn(getMockVersion());
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when(getMockCluster().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getBytes()).thenReturn(data);


        testGetFileMessage(Cluster.NAME);
    }

    @Test
    public void testGetUsersFile() throws MirandaException {
        setupMockMiranda();
        setupMockUserManager();
        setupMockSingleFile();
        setupMockVersion();

        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
        when(getMockUserManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getBytes()).thenReturn(data);
        when(getMockSingleFile().getVersion()).thenReturn(getMockVersion());
        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
        when(getMockUserManager().getFile()).thenReturn(getMockSingleFile());

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

        assert (nextState instanceof NodeReadyState);
    }

    @Test
    public void testProcessGetFileResonseMessage() throws MirandaException {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(null, this, "whatever");
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(Files.Topic, new Version(), "hi there".getBytes()) ;

        State nextState = getReadyState().processMessage(getFileResponseMessage);

        assert (nextState instanceof NodeReadyState);

}

    @Test
    public void testGetVersionsWireMessage() throws MirandaException {
        setupMockMiranda();
        setupMockTopicsManager();
        setupMockSingleFile();
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        NetworkMessage networkMessage = new NetworkMessage(null, this, getVersionsWireMessage);
        setupMockVersion ();
        BlockingQueue<Message> queue = new LinkedBlockingDeque<Message>();

        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
        when (getMockTopicManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockMiranda().getQueue()).thenReturn(queue);

        setupMockDeliveryManager();
       when(getMockMiranda().getDeliveryManager()).thenReturn(getMockDeliveryManager());
        when (getMockDeliveryManager().getFile()).thenReturn(getMockFile());
        when (getMockFile().getVersion()).thenReturn(getMockVersion());

        setupMockCluster();
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
        when(getMockCluster().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getVersion()).thenReturn(getMockVersion());

        setupMockSubscriptionManager();
        when(getMockMiranda().getSubscriptionManager()).thenReturn(getMockSubscriptionManager());
        when(getMockSubscriptionManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getVersion()).thenReturn(getMockVersion());

        setupMockUserManager();
        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
        when(getMockUserManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getVersion()).thenReturn(getMockVersion());

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

        WireMessage wireMessage = null;
        if (file.equalsIgnoreCase(Cluster.NAME)) {
            setupMockCluster();
            when(getMockMiranda().getCluster()).thenReturn(getMockCluster());
            when(getMockCluster().getQueue()).thenReturn(queue);
            when(getMockCluster().getClusterFile()).thenReturn(getMockClusterfile());
            wireMessage = new GetFileWireMessage(Files.Cluster);
        } else if (file.equalsIgnoreCase(UsersFile.FILE_NAME)) {
            setupMockUsersFile();
            when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
            when(getMockUserManager().getFile()).thenReturn(getMockUsersFile());
            when(getMockUsersFile().getQueue()).thenReturn(queue);
            wireMessage = new GetFileWireMessage(Files.User);
        } else if (file.equalsIgnoreCase(TopicsFile.FILE_NAME)) {
            setupMockTopicsFile();
            when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
            when(getMockTopicManager().getFile()).thenReturn(getMockTopicsFile());
            when(getMockTopicsFile().getQueue()).thenReturn(queue);
            wireMessage = new GetFileWireMessage(Files.Topic);
        } else if (file.equalsIgnoreCase(SubscriptionsFile.FILE_NAME)) {
            setupMockSubscriptionsFile();
            when(getMockMiranda().getSubscriptionManager()).thenReturn(getMockSubscriptionManager());
            when(getMockSubscriptionManager().getFile()).thenReturn(getMockSubscriptionsFile());
            when(getMockSubscriptionsFile().getQueue()).thenReturn(queue);
            wireMessage = new GetFileWireMessage(Files.Subscription);
        } else {
            System.err.println("Unrecognized file: " + file);
            System.exit(1);
        }




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
        setupMockTopicsManager();
        setupMockTopicsFile();
        setupMockSingleFile();
        setupMockCluster();
        setupMockClutersfile();

        when (getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
        when (getMockTopicManager().getTopicsFile()).thenReturn(getMockTopicsFile());
        when (getMockCluster().getQueue()).thenReturn(queue);
        when (getMockCluster().getClusterFile()).thenReturn(getMockClusterfile());

        testProcessGetFileWireMessage(Cluster.NAME);
    }

    @Test
    public void testProcessGetFileWireMessageUsers() throws MirandaException {
        setupMockMiranda();
        setupMockTopicsManager();
        setupMockSingleFile();
        setupMockTopicsManager();
        setupMockVersion();
        setupMockUserManager();

        byte[] data = {1,2,3};

        when(getMockSingleFile().getBytes()).thenReturn(data);
        when (getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
        when(getMockTopicManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getVersion()).thenReturn(getMockVersion());
        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
        when(getMockUserManager().getUsersFile()).thenReturn(getMockUsersFile());

        testProcessGetFileWireMessage(UsersFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageTopics() throws MirandaException {
        setupMockMiranda();
        setupMockTopicsManager();
        setupMockSingleFile();
        setupMockVersion();
        setupMockVersion();

        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
        when(getMockTopicManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getBytes()).thenReturn(data);
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
        when(getMockTopicManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getVersion()).thenReturn(getMockVersion());
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        testProcessGetFileWireMessage(TopicsFile.FILE_NAME);
    }

    @Test
    public void testProcessGetFileWireMessageSubscriptions() throws MirandaException {
        setupMockMiranda();
        setupMockTopicsManager();
        setupMockTopicsFile();
        setupMockSubscriptionManager();

        when(getMockTopicManager().getTopicsFile()).thenReturn(getMockTopicsFile());
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());
        when(getMockTopicManager().getFile()).thenReturn(getMockSingleFile());
        when(getMockMiranda().getSubscriptionManager()).thenReturn(getMockSubscriptionManager());
        when(getMockSubscriptionManager().getSubscriptionsFile()).thenReturn(getMockSubscriptionsFile());

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
        verify(getMockMiranda(), atLeastOnce()).sendAddSessionMessage(Matchers.any(), Matchers.any(), Matchers.eq(session));
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
        verify(getMockMiranda(), atLeastOnce()).sendSessionsExpiredMessage(Matchers.any(), Matchers.any(), Matchers.eq(expiredSessions));
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
        verify(getMockMiranda(), atLeastOnce()).sendUserAddedMessage(Matchers.any(),
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
        verify(getMockMiranda(), atLeastOnce()).sendUserUpdatedMessage(Matchers.any(),
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
        verify(getMockMiranda(), atLeastOnce()).sendUserDeletedMessage(Matchers.any(),
                Matchers.any(), Matchers.eq("whatever"));
    }
}
