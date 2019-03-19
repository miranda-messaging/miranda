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

import com.ltsllc.commons.util.ImprovedRandom;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
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
import com.ltsllc.miranda.file.messages.Notification;
import com.ltsllc.miranda.network.messages.NodeAddedMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.StartConversationMessage;
import com.ltsllc.miranda.node.messages.UserDeletedMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.servlet.status.GetStatusMessage;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.topics.messages.CreateTopicMessage;
import com.ltsllc.miranda.topics.messages.DeleteTopicMessage;
import com.ltsllc.miranda.topics.messages.NewTopicMessage;
import com.ltsllc.miranda.topics.messages.UpdateTopicMessage;
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
import java.util.IdentityHashMap;
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
    public void oldtestUpdateTopic () throws MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        Topic topic = Topic.random(improvedRandom);
        UpdateTopicMessage updateTopicMessage = new UpdateTopicMessage(null, null, null, topic);
        State nextState = getClusterReadyState().processMessage(updateTopicMessage);
        verify(getMockCluster(), atLeastOnce()).broadcast(any(WireMessage.class));
    }

    @Test
    public void oldtestDeleteTopic () throws MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        Topic topic = Topic.random(improvedRandom);
        DeleteTopicMessage deleteTopicMessage = new DeleteTopicMessage(null,null,null, topic.getName());
        State nextState = getClusterReadyState().processMessage(deleteTopicMessage);
        verify(getMockCluster(), atLeastOnce()).broadcast(any(WireMessage.class));
    }
/*
    @Test
    public void testNewTopic () throws MirandaException {
        setupMiranda();
        setupMockPanicPolicy();
        when(getMockCluster().processMessage(any())).thenReturn(getClusterReadyState());
        ImprovedRandom improvedRandom = new ImprovedRandom();
        Topic topic = Topic.random(improvedRandom);
        NewTopicMessage newTopicMessage = new NewTopicMessage(null, null, topic);
        getClusterReadyState().processMessage(newTopicMessage);
        verify(getMockCluster(), atLeastOnce()).broadcast(any(WireMessage.class));
    }
*/
    @Test
    public void testProcessShutdownMessage () throws MirandaException {
        ShutdownMessage shutdownMessage = new ShutdownMessage(null, this);

        State nextState = getClusterReadyState().processMessage(shutdownMessage);

        assert (nextState instanceof ClusterShutdownState);
    }

    @Test
    public void testLoad () throws MirandaException {
        LoadMessage loadMessage = new LoadMessage(null,null);

        State nextState = getClusterReadyState().processMessage(loadMessage);

        verify(getMockCluster(), atLeastOnce()).load();
    }


    @Test
    public void testConnect () throws MirandaException {
        ConnectMessage connectMessage = new ConnectMessage(null,null);

        State nextState = getClusterReadyState().processMessage(connectMessage);
        verify(getMockCluster(), atLeastOnce()).connect();
    }


    @Test
    public void testGetVersion () throws MirandaException {
        GetVersionMessage getVersionMessage = new GetVersionMessage(null,null, null);
        when(getMockCluster().getFile()).thenReturn(getMockClusterFile());
        when(getMockClusterFile().getQueue()).thenReturn(new LinkedBlockingQueue<>());
        getClusterReadyState().processMessage(getVersionMessage);
    }

    @Test
    public void testClusterFileChanged () throws MirandaException{
        ClusterFileChangedMessage clusterFileChangedMessage = new ClusterFileChangedMessage(null, null,
                new ArrayList<NodeElement>(), new Version());
        when (getMockCluster().getFile()).thenReturn(getMockClusterFile());
        when (getMockCluster().getCurrentState()).thenReturn(getClusterReadyState());
        getClusterReadyState().processMessage(clusterFileChangedMessage);

        verify(getMockCluster(),atLeastOnce()).merge(new ArrayList<NodeElement>());
    }

    @Test
    public void testHealthCheck () throws MirandaException{
        HealthCheckMessage healthCheckMessage = new HealthCheckMessage(null, null);
        getClusterReadyState().processMessage(healthCheckMessage);
        verify(getMockCluster(),atLeastOnce()).performHealthCheck();
    }

    @Test
    public void testDropNode () throws MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        NodeElement nodeElement = new NodeElement(improvedRandom);
        DropNodeMessage dropNodeMessage = new DropNodeMessage(null,null, nodeElement);
        ArrayList list = new ArrayList();
        list.add(getMockNode());
        when(getMockCluster().getNodes()).thenReturn(list);
        assert (list.contains(getMockNode()));

        getClusterReadyState().processMessage(dropNodeMessage);

        assert (list.size() == 0);

    }

    @Test
    public void testNodeAdded () throws MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        NodeElement  nodeElement = new NodeElement(improvedRandom);
        Node newNode = new Node(nodeElement, getMockNetwork(), getMockCluster());
        NodeAddedMessage nodeAddedMessage = new NodeAddedMessage(null, null, newNode);
        List list = new ArrayList();
        when(getMockCluster().getNodes()).thenReturn(list);
        assert(list.size() == 0);

        getClusterReadyState().processMessage(nodeAddedMessage);
        // verify(getMockCluster(), atLeastOnce()).
        assert(list.size() == 1);
    }

    @Test
    public void testStatus ()throws MirandaException {
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        GetStatusMessage getStatusMessage = new GetStatusMessage(queue,null);
        getClusterReadyState().processMessage(getStatusMessage);
        assert(queue.size() == 1);
    }




    @Test
    public void testAddSession () throws MirandaException{
        Session session = new Session(null, -1,-1);
        AddSessionMessage addSessionMessage = new AddSessionMessage(null, null, session);

        getClusterReadyState().processMessage(addSessionMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());

    }

    @Test
    public void testSessionsExpired () throws MirandaException{
        Session session = new Session(null, -1,-1);
        List l = new ArrayList();
        l.add(session);
        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(null, null, l);

        getClusterReadyState().processMessage(sessionsExpiredMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());

    }

    @Test
    public void testAddUser () throws GeneralSecurityException, MirandaException{
        ImprovedRandom improvedRandom = new ImprovedRandom();
        User user = User.createRandom(improvedRandom);
        NewUserMessage newUserMessage = new NewUserMessage(null, null,user);
        getClusterReadyState().processMessage(newUserMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());
    }

    @Test
    public void testUpdateUser () throws GeneralSecurityException, MirandaException{
        ImprovedRandom improvedRandom = new ImprovedRandom();
        User user = User.createRandom(improvedRandom);
        UpdateUserMessage updateUserMessage = new UpdateUserMessage(null, null, null, user);

        getClusterReadyState().processMessage(updateUserMessage);

        verify (getMockCluster(),atLeastOnce()).broadcast(any());
    }

    @Test
    public void testDeleteUser () throws GeneralSecurityException, MirandaException{
        ImprovedRandom improvedRandom = new ImprovedRandom();
        User user = User.createRandom(improvedRandom);
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage(null,null,null, user.getName());

        getClusterReadyState().processMessage(deleteUserMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());
    }


    @Test
    public void testShutdown () throws MirandaException{
        ShutdownMessage shutdownMessage = new ShutdownMessage(null, null);

        State nextState = getClusterReadyState().processMessage(shutdownMessage);

        assert (nextState instanceof ClusterShutdownState);
    }

    @Test
    public void testAddSubscription () throws GeneralSecurityException, MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();

        Subscription subscription = Subscription.createRandom(improvedRandom);
        Session session = Session.random(improvedRandom);
        setupMiranda();
        setupMockCluster();

        CreateSubscriptionMessage createSubscriptionMessage = new CreateSubscriptionMessage(null,null,
                null, null);
        getClusterReadyState().processMessage(createSubscriptionMessage);


        verify(getMockCluster(), atLeastOnce()).broadcast(any());
    }

    @Test
    public void testUpdateSubscription () throws MirandaException {
          ImprovedRandom improvedRandom = new ImprovedRandom();

          Subscription subscription1 = Subscription.createRandom(improvedRandom);
          Subscription subscription2 = Subscription.createRandom(improvedRandom);

         UpdateSubscriptionMessage updateSubscriptionMessage = new UpdateSubscriptionMessage(null,
                 null, null, subscription2);

         getClusterReadyState().processMessage(updateSubscriptionMessage);

         verify(getMockCluster(), atLeastOnce()).broadcast(any());
    }



    @Test
    public void testDeleteSubscription () throws MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        Subscription subscription = Subscription.createRandom(improvedRandom);

        DeleteSubscriptionMessage deleteSubscriptionMessage = new DeleteSubscriptionMessage(null,null,
                null, improvedRandom.randomString(8));

        getClusterReadyState().processMessage(deleteSubscriptionMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());
    }

    @Test
    public void testAddTopic () throws MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        Topic topic = Topic.random(improvedRandom);
        CreateTopicMessage createTopicMessage = new CreateTopicMessage(null,null,
                null, topic);

        getClusterReadyState().processMessage(createTopicMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());
    }

    @Test
    public void testUpdateTopic () throws MirandaException{
        ImprovedRandom improvedRandom = new ImprovedRandom();
        Topic topic = Topic.random(improvedRandom);
        UpdateTopicMessage updateTopicMessage = new UpdateTopicMessage (null, null,
                null, topic);

        getClusterReadyState().processMessage(updateTopicMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());

    }

    @Test
    public void testDeleteTopic () throws MirandaException{
        ImprovedRandom improvedRandom = new ImprovedRandom();
        DeleteTopicMessage deleteTopicMessage = new DeleteTopicMessage(null,null,
                null, improvedRandom.randomString(8));

        getClusterReadyState().processMessage(deleteTopicMessage);

        verify(getMockCluster(), atLeastOnce()).broadcast(any());
    }

    public void testStartConversation () throws MirandaException {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        StartConversationMessage startConversationMessage = new StartConversationMessage(null, null,
                null, null);

        verify(getMockCluster(), never()).broadcast(any());

        getClusterReadyState().processMessage(startConversationMessage);

        verify(getMockCluster(), never()).broadcast(any());
    }

    public void testEndConversation () {

    }
}
