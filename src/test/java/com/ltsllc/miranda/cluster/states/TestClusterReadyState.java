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

package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.ClusterStatusObject;
import com.ltsllc.miranda.clientinterface.objects.NodeStatus;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.TestClusterFile;
import com.ltsllc.miranda.cluster.messages.*;
import com.ltsllc.miranda.cluster.networkMessages.DeleteUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.NewUserWireMessage;
import com.ltsllc.miranda.cluster.networkMessages.UpdateUserWireMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.servlet.status.GetStatusMessage;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.messages.DeleteUserMessage;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.user.messages.UpdateUserMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestClusterReadyState extends TestCase {
    private static final String PROPERTIES_FILENAME = "junk.properties";

    @Mock
    private Node mockNode;

    @Mock
    private ClusterFile mockClusterFile;

    private ClusterReadyState clusterReadyState;

    public ClusterFile getMockClusterFile() {
        return mockClusterFile;
    }

    public ClusterReadyState getClusterReadyState() {
        return clusterReadyState;
    }

    public Node getMockNode() {
        return mockNode;
    }

    public void reset() throws Exception {
        super.reset();

        this.mockClusterFile = null;
        this.mockNode = null;

        deleteFile(PROPERTIES_FILENAME);
    }

    @Before
    public void setup() {
        try {
            reset();

            super.setup();

            setuplog4j();
            setupMirandaProperties();
            setupKeyStore();
            setupTrustStore();

            this.mockClusterFile = mock(ClusterFile.class);
            this.mockNode = mock(Node.class);
            this.clusterReadyState = new ClusterReadyState(getMockCluster());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanup() {
        cleanupTrustStore();
    }

    @Test
    public void testProcessLoad() throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        LoadMessage message = new LoadMessage(queue, this);

        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).load();
    }

    /**
     * When a cluster gets a {@link ConnectMessage}, it should drop everything
     * and connect.
     */
    @Test
    public void testProcessMessageConnect() throws MirandaException {
        ConnectMessage message = new ConnectMessage(null, this);

        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).connect();
    }


    /**
     * The requester of this message wants version information about the
     * {@link TestClusterFile}.  Ensure that we sendToMe a
     * {@link Message.Subjects#Version} message, and that
     * we sendToMe it to the right place.
     */
    @Test
    public void testProcessMessageGetVersion() throws MirandaException {
        GetVersionMessage message = new GetVersionMessage(null, this, null);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        when(getMockCluster().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getQueue()).thenReturn(queue);

        getClusterReadyState().processMessage(message);

        assert (contains(Message.Subjects.GetVersion, queue));
    }


    /**
     * This message is sent when we should "garbage collect" our nodes.
     * The ssltest needs to enusure that a {@link HealthCheckUpdateMessage}
     * is sent to the cluster file.
     */
    @Test
    public void testProcessMessageHealthCheck() throws MirandaException {
        HealthCheckMessage message = new HealthCheckMessage(null, this);

        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).performHealthCheck();
    }

    @Test
    public void testProcessConnect () throws MirandaException {
        List<Node> nodeList = new ArrayList<Node>();
        nodeList.add(getMockNode());
        ConnectMessage connectMessage = new ConnectMessage(null, this);

        getClusterReadyState().processMessage(connectMessage);

        verify(getMockCluster(), atLeastOnce()).connect();
    }

    @Test
    public void testProcessLoadResponseMessage () throws MirandaException {
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("bar.com", 6790, "another node");
        nodeElementList.add(nodeElement);
        LoadResponseMessage loadResponseMessage = new LoadResponseMessage(null, this, nodeElementList);

        getClusterReadyState().processMessage(loadResponseMessage);

        verify(getMockCluster(), atLeastOnce()).merge(Matchers.anyList());
    }

    @Test
    public void testProcessGetVersionMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        GetVersionMessage getVersionMessage = new GetVersionMessage(queue, this, queue);

        when(getMockCluster().getFile()).thenReturn(getMockSingleFile());
        when(getMockSingleFile().getQueue()).thenReturn(queue);
        when(getMockCluster().getQueue()).thenReturn(queue);

        getClusterReadyState().processMessage(getVersionMessage);

        verify(getMockCluster(), atLeastOnce()).getQueue();
        verify(getMockCluster(), atLeastOnce()).getFile();
    }

    @Test
    public void testClusterFileChangedMessage () throws MirandaException {
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("bar.com", 6790, "another node");
        nodeElementList.add(nodeElement);
        Version version = new Version();

        ClusterFileChangedMessage clusterFileChangedMessage = new ClusterFileChangedMessage(null, this, nodeElementList, version);

        getClusterReadyState().processMessage(clusterFileChangedMessage);

        verify(getMockCluster(), atLeastOnce()).merge(Matchers.anyList());
    }

    @Test
    public void testProcessDropNodeMessage () throws MirandaException {
        NodeElement nodeElement = new NodeElement("bar.com", 6790, "another node");
        DropNodeMessage dropNodeMessage = new DropNodeMessage(null, this, nodeElement);
        List<Node> nodeList = new ArrayList<Node>();

        when(getMockCluster().matchingNode(nodeElement)).thenReturn(null);

        getClusterReadyState().processMessage(dropNodeMessage);

        verify(getMockCluster(), atLeastOnce()).matchingNode(Matchers.any(NodeElement.class));

        when(getMockCluster().matchingNode(nodeElement)).thenReturn(getMockNode());
        when(getMockNode().isConnected()).thenReturn(false);
        when(getMockCluster().getNodes()).thenReturn(nodeList);

        getClusterReadyState().processMessage(dropNodeMessage);

        verify(getMockCluster(), atLeastOnce()).matchingNode(Matchers.any(NodeElement.class));
        verify(getMockNode(), atLeastOnce()).isConnected();
        verify(getMockCluster(), atLeastOnce()).getNodes();

        when(getMockCluster().matchingNode(nodeElement)).thenReturn(getMockNode());
        when(getMockNode().isConnected()).thenReturn(true);

        getClusterReadyState().processMessage(dropNodeMessage);

        verify(getMockCluster(), atLeastOnce()).matchingNode(Matchers.any(NodeElement.class));
        verify(getMockNode(), atLeastOnce()).isConnected();
    }

    @Test
    public void testProcessGetStatusMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        List<NodeStatus> nodeStatusList = new ArrayList<NodeStatus>();
        GetStatusMessage getStatusMessage = new GetStatusMessage(queue, this);
        ClusterStatusObject clusterStatusObject = new ClusterStatusObject(nodeStatusList);

        when(getMockCluster().getStatus()).thenReturn(clusterStatusObject);

        getClusterReadyState().processMessage(getStatusMessage);

        verify(getMockCluster(), atLeastOnce()).getStatus();
    }

    @Test
    public void testProcessNewSessionMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY_PEM);
        User user = userObject.asUser();

        Session session = new Session(user,123, 456);

        AddSessionMessage addSessionMessage = new AddSessionMessage(null, this, session);

        when(getMockCluster().getCurrentState()).thenReturn(getClusterReadyState());

        State nextState = getClusterReadyState().processMessage(addSessionMessage);

        assert (nextState instanceof ClusterReadyState);
        verify(getMockCluster(), atLeastOnce()).broadcast(Matchers.any(WireMessage.class));
    }

    @Test
    public void testProcessSessionsExpired () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher","whatever", TEST_PUBLIC_KEY_PEM);
        User user = userObject.asUser();

        Session session = new Session(user,123, 456);
        List<Session> exipiredSessions = new ArrayList<Session>();
        exipiredSessions.add(session);
        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(null,this, exipiredSessions);

        when(getMockCluster().getCurrentState()).thenReturn(getClusterReadyState());

        State nextState = getClusterReadyState().processMessage(sessionsExpiredMessage);

        assert (nextState instanceof ClusterReadyState);
        verify(getMockCluster(), atLeastOnce()).broadcast(Matchers.any(WireMessage.class));
    }

    public static final String TEST_PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----\n"
    + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1avWB4w2AtIN/DOSyyDu\n"
    + "dN7OA3XVbjyq9cKkVkLtHuKQYvq2w1sFoToeZ15R+J7WxDGFuSzdWa/RbR5LLNeM\n"
    + "BqgGZ+/jwGOipRtUMVa8467ZV5BL6vowkjAyUUevTABUxzTo+YvwrL8LPVpNOO1v\n"
    + "VmAsWOe+lTyeQkAILaSeCvyjdmDRr5O5U5UILlAcZDJ8LFOm9kNQQ4yIVUqAMbBo\n"
    + "MF+vPrmEA09tMqrmR5lb4RsmAUlDxiMWCU9AxwWfksHbd7fV8puvnxjuI1+TZ7SS\n"
    + "Fk1L/bPothhCjsWYr4RMVDluzSAgqsFbAgLXGpraDibVOOrmmBtG2ngu9NJV5fGA\n"
    + "NwIDAQAB\n"
    + "-----END PUBLIC KEY-----\n";


    @Test
    public void testProcessNewUserMessage () throws MirandaException {
        UserObject userObject = new UserObject("test", "Publisher", "a test user", TEST_PUBLIC_KEY_PEM);
        User user = userObject.asUser();
        NewUserMessage newUserMessage = new NewUserMessage(null, this, user);
        when(getMockCluster().getCurrentState()).thenReturn(getClusterReadyState());

        State nextState = getClusterReadyState().processMessage(newUserMessage);

        assert(nextState == getClusterReadyState());
        verify(getMockCluster(), atLeastOnce()).broadcast(Matchers.any(NewUserWireMessage.class));
    }

    @Test
    public void testProcessUpdateUserMessage () throws MirandaException, GeneralSecurityException {
        UserObject userObject = new UserObject("test", "Publisher","a test user", TEST_PUBLIC_KEY_PEM);
        User user = userObject.asUser();
        UpdateUserMessage updateUserMessage = new UpdateUserMessage(null, this, null, user);
        when(getMockCluster().getCurrentState()).thenReturn(getClusterReadyState());

        State nextState = getClusterReadyState().processMessage(updateUserMessage);

        assert (nextState == getClusterReadyState());
        verify(getMockCluster(), atLeastOnce()).broadcast(Matchers.any(UpdateUserWireMessage.class));
    }

    @Test
    public void testProcessDeleteUserMessage () throws MirandaException {
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage(null, this, getMockSession(), "test");

        when(getMockCluster().getCurrentState()).thenReturn(getClusterReadyState());

        State nextState = getClusterReadyState().processMessage(deleteUserMessage);

        assert (nextState == getClusterReadyState());
        verify(getMockCluster(), atLeastOnce()).broadcast(any(DeleteUserWireMessage.class));
    }

    public boolean containsNode (List<Node> nodeList, Node node) {
        for (Node element : nodeList) {
            if (element.equals(node))
                return true;
        }

        return false;
    }

    @Test
    public void testProcessNewNodeMessage () throws MirandaException {
        NodeElement nodeElement = new NodeElement("foo.com",6789, "a test node");
        Node node = new Node(nodeElement, getMockNetwork(), getMockCluster());
        NewNodeMessage newNodeMessage = new NewNodeMessage(null, this, node);
        List<Node> nodes = new ArrayList<Node>();
        when(getMockCluster().getNodes()).thenReturn(nodes);

        State nextState = getClusterReadyState().processMessage(newNodeMessage);

        assert (containsNode(nodes, node));
    }

    @Test
    public void testProcessShutdownMessage () throws MirandaException {
        ShutdownMessage shutdownMessage = new ShutdownMessage(null, this);

        when(getMockCluster().getClusterFile()).thenReturn(getMockClusterFile());
        State nextState = getClusterReadyState().processMessage(shutdownMessage);

        verify(getMockCluster(), atLeastOnce()).shutdown();
        verify(getMockCluster(), atLeastOnce()).setClusterFileResponded(Matchers.eq(false));
        verify(getMockClusterFile(), atLeastOnce()).sendShutdown(Matchers.any(BlockingQueue.class), Matchers.any());
    }
}
