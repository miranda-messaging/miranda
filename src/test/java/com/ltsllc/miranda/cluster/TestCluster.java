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

package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.*;
import com.ltsllc.miranda.clientinterface.objects.ClusterStatusObject;
import com.ltsllc.miranda.clientinterface.objects.NodeStatus;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.NewSessionWireMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.messages.UpdateUserMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestCluster extends TestCase {
    private Cluster cluster;

    @Mock
    private ClusterFile mockClusterFile;

    @Mock
    private Node mockNode;

    @Mock
    private Node mockNode2;

    public Node getMockNode2() {
        return mockNode2;
    }

    public ClusterFile getMockClusterFile() {
        return mockClusterFile;
    }

    public Node getMockNode() {
        return mockNode;
    }

    public com.ltsllc.miranda.cluster.Cluster getCluster() {
        return cluster;
    }

    private static final String CLUSTER_FILENAME = "cluster.json";

    private static final String[] CLUSTER_FILE_CONTENTS = {
            "[",
            "    {",
            "        \"dns\" : \"foo.com\",",
            "        \"ip\" : \"192.168.1.1\",",
            "        \"port\" : 6789,",
            "        \"description\" : \"a ssltest node\",",
            "        \"expires\" : " + Long.MAX_VALUE,
            "    }",
            "]"
    };


    public void reset () throws Exception {
        super.reset();

        this.cluster = null;
        this.mockClusterFile = null;
        this.mockNode = null;
        this.mockNode2 = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();
        setupMockWriter();
        setupMockProperties();
        setupMockMiranda();
        setupMockTimer();

        deleteFile(CLUSTER_FILENAME);

        this.mockClusterFile = mock(ClusterFile.class);
        this.mockNode = mock(Node.class);

        this.cluster = new Cluster(getMockNetwork(), true);

        getCluster().setFile(getMockClusterFile());

        this.mockNode2 = mock(Node.class);
    }

    @After
    public void cleanup () {
        cleanupTrustStore();
        deleteFile(CLUSTER_FILENAME);
    }

    @Test
    public void testConstructor () {
        assert (null != getCluster().getClusterFile());
        verify (getMockClusterFile(), atLeastOnce()).addSubscriber(Matchers.any(BlockingQueue.class));
    }

    /**
     * Ensure that we create a load message.
     */
    @Test
    public void testLoad () {
        getCluster().setFile(getMockClusterFile());

        getCluster().load();

        verify(getMockClusterFile(), atLeastOnce()).sendLoad(Matchers.any(BlockingQueue.class), Matchers.any());
    }

    @Test
    public void testContains () throws MirandaException {
        NodeElement shouldContain = new NodeElement("foo.com", 6789, "a node");
        Node node = new Node(shouldContain, getMockNetwork(), getMockCluster());
        NodeElement shouldNotContain = new NodeElement("bar.com", 6790, "another node");
        List<Node> nodeList = new ArrayList<Node>(1);
        nodeList.add(node);

        getCluster().setData(nodeList);

        assert (getCluster().contains(shouldContain));
        assert (!getCluster().contains(shouldNotContain));
    }

    @Test
    public void testConnect () {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(getMockNode());

        getCluster().setData(nodes);
        getCluster().connect();

        verify(getMockNode(), atLeastOnce()).connect();
    }

    @Test
    public void testMatchingNode () throws MirandaException {
        ArrayList<Node> nodes = new ArrayList<Node>();
        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");
        Node node = new Node(nodeElement, getMockNetwork(), getCluster());
        nodes.add(node);

        getCluster().setData(nodes);
        Node match = getCluster().matchingNode(nodeElement);

        assert(match != null);
    }

    @Test
    public void testGetStatus () throws MirandaException {
        ArrayList<Node> nodes = new ArrayList<Node>();
        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");
        Node node = new Node(nodeElement, getMockNetwork(), getCluster());
        nodes.add(node);

        getCluster().setData(nodes);

        NodeStatus nodeStatus = node.getStatus();

        ClusterStatusObject clusterStatusObject = getCluster().getStatus();
        assert (containsStatus(nodeStatus, clusterStatusObject));
    }

    public boolean containsNodeElement (NodeElement nodeElement, List<Node> nodes) {
        for (Node node : nodes) {
            if (node.matches(nodeElement))
                return true;
        }

        return false;
    }

    @Test
    public void testMergeNewNode () throws MirandaException {
        List<Node> nodes = new ArrayList<Node>();
        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");
        Node node = new Node(nodeElement, getMockNetwork(), getCluster());
        nodes.add(node);

        getCluster().setData(nodes);

        NodeElement newNode = new NodeElement("foo.com", 6789, "another node");
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();
        nodeElements.add(newNode);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        when(getMockClusterFile().getQueue()).thenReturn(queue);

        getCluster().merge(nodeElements);

        assert(containsNodeElement(newNode, getCluster().getNodes()));
        assert(contains(Message.Subjects.NodesUpdated, queue));
    }

    @Test
    public void testMergeNoChange () throws MirandaException {
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        when(getMockClusterFile().getQueue()).thenReturn(queue);

        getCluster().merge(nodeElements);

        assert (!contains(Message.Subjects.NodesUpdated, queue));
    }

    @Test
    public void testNewNode () throws MirandaException {
        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");
        Node node = new Node(nodeElement, getMockNetwork(), getCluster());

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        when(getMockClusterFile().getQueue()).thenReturn(queue);

        getCluster().newNode(node);

        Node matchingNode = getCluster().matchingNode(nodeElement);

        assert (matchingNode != null);
        assert (contains(Message.Subjects.NodesUpdated, queue));
    }

    @Test
    public void testSendNewNode () throws MirandaException {
        getCluster().stop();

        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");
        Node node = new Node(nodeElement, getMockNetwork(), getCluster());

        getCluster().sendNewNode(null, this, node);

        assert (contains(Message.Subjects.NewNode, getCluster().getQueue()));
    }

    @Test
    public void testPerformHealthCheckUpdated () {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(getMockNode());
        getCluster().setData(nodes);

        when(getMockNode().isConnected()).thenReturn(true);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        when(getMockClusterFile().getQueue()).thenReturn(queue);

        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");

        when(getMockNode().asNodeElement()).thenReturn(nodeElement);

        getCluster().performHealthCheck();

        assert (contains(Message.Subjects.NodesUpdated, queue));
    }

    @Test
    public void testPerformHealthCheckNoUpdate () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        when(getMockClusterFile().getQueue()).thenReturn(queue);

        getCluster().performHealthCheck();

        assert(!contains(Message.Subjects.NodesUpdated, queue));
    }

    @Test
    public void testSendConnect () {
        getCluster().stop();

        getCluster().sendConnect(null, this);

        assert (contains(Message.Subjects.Connect, getCluster().getQueue()));
    }

    @Test
    public void testSendGetStatus () {
        getCluster().stop();

        getCluster().sendGetStatus(null, this);

        assert (contains(Message.Subjects.GetStatus, getCluster().getQueue()));
    }

    @Test
    public void testStop () {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(getMockNode());
        getCluster().setData(nodes);

        getCluster().stop();

        verify(getMockNode(), atLeastOnce()).sendStop(Matchers.any(BlockingQueue.class), Matchers.any());
    }

    @Test
    public void testSendNodeStopped () throws MirandaException {
        getCluster().stop();

        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");
        Node node = new Node(nodeElement, getMockNetwork(), getCluster());

        getCluster().sendNodeStopped(null, this, node);

        assert (contains(Message.Subjects.NodeStopped, getCluster().getQueue()));
    }

    @Test
    public void testSendNewSession () {
        getCluster().stop();

        User user = new User("joe", "whatever");
        Session session = new Session(user, 0, 123);

        getCluster().sendNewSession(null, this, session);

        assert (contains(Message.Subjects.AddSession, getCluster().getQueue()));
    }

    @Test
    public void testSendSessionsExpired () {
        getCluster().stop();

        User user = new User("joe", "whatever");
        Session session = new Session(user, 0, 123);
        List<Session> expiredSessions = new ArrayList<Session>();
        expiredSessions.add(session);

        getCluster().sendSessionsExpiredMessage(null, this, expiredSessions);

        assert (contains(Message.Subjects.SessionsExpired, getCluster().getQueue()));
    }

    /*
    @Test
    public void testAsNodeElements () throws MirandaException {
        List<Node> nodes = new ArrayList<Node>();
        NodeElement nodeElement = new NodeElement("whatever.com", 6789, "whatever");
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        nodeElementList.add(nodeElement);
        Node node = new Node(nodeElement, getMockNetwork(), getCluster());
        nodes.add(node);

        getCluster().setData(nodes);

        List<NodeElement> nodeElements = getCluster().asNodeElements();


        assert(listsAreEquivalent(nodeElements, nodeElementList));
    }
    */


    @Test
    public void testBroadcast () throws MirandaException {
        getCluster().getNodes().clear();
        getCluster().getNodes().add(getMockNode());
        getCluster().getNodes().add(getMockNode2());

        long now = System.currentTimeMillis();

        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        Session session = new Session(user, now + 120000, 123);
        NewSessionWireMessage newSessionWireMessage = new NewSessionWireMessage(session);
        getCluster().broadcast(newSessionWireMessage);

        verify(getMockNode(), atLeastOnce()).sendSendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(newSessionWireMessage));
        verify(getMockNode2(), atLeastOnce()).sendSendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(newSessionWireMessage));
    }

    public static final String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpjR9MH5cTEPIXR/0cLp/Lw3QDK4RMPIygL8Aqh0yQ/MOpQtXrBzwSph4N1NURg1tB3EuyCVGsTfSfrbR5nqsN5IiaJyBuvhThBLwHyKN+PEUQ/rB6qUyg+jcPigTfqj6gksNxnC6CmCJ6XpBOiBOORgFQvdISo7pOqxZKxmaTqwIDAQAB";

    @Test
    public void testSendNewUserMessage () throws MirandaException {
        getCluster().stop();
        UserObject userObject = new UserObject("test", "Publisher","a test user", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        getCluster().sendNewUserMessage(null, this, user);

        assert (contains(Message.Subjects.NewUser, getCluster().getQueue()));
    }

    @Test
    public void testSendUpdateUserMessage () throws MirandaException {
        getCluster().stop();
        UserObject userObject = new UserObject("test", "Publisher","a test user", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        UpdateUserMessage updateUserMessage = new UpdateUserMessage(null, this, null, user);

        getCluster().sendUpdateUserMessage(null, this, user);

        assert (contains(Message.Subjects.UpdateUser, getCluster().getQueue()));
    }

    @Test
    public void testSendDeleteUserMessage () {
        getCluster().stop();

        getCluster().sendDeleteUserMessage(null, this, getMockSession(), "joe");

        assert (contains(Message.Subjects.DeleteUser, getCluster().getQueue()));
    }

    @Test
    public void testSendNewTopicMessage () {
        getCluster().stop();

        Topic topic = new Topic ("whatever", "joe");

        getCluster().sendNewTopicMessage(null, this, topic);

        assert (contains(Message.Subjects.NewTopic, getCluster().getQueue()));
    }

    @Test
    public void testSendCreateSubscriptionMessage () {
        getCluster().stop();

        Subscription subscription = new Subscription("whatever");

        getCluster().sendCreateSubscriptionMessage(null, this, getMockSession(), subscription);

        assert (contains(Message.Subjects.CreateSubscription, getCluster().getQueue()));
    }

    @Test
    public void testSendUpdateSubscription () {
        getCluster().stop();

        Subscription subscription = new Subscription("whatever");

        getCluster().sendUpdateSubscriptionMessage(null, this, getMockSession(), subscription);

        assert (contains(Message.Subjects.UpdateSubscription, getCluster().getQueue()));
    }

    @Test
    public void testSendDeleteSubscription () {
        getCluster().stop();

        Subscription subscription = new Subscription("whatever");

        getCluster().sendDeleteSubscriptionMessage(null, this, getMockSession(), "whatever");

        assert (contains(Message.Subjects.DeleteSubscription, getCluster().getQueue()));
    }

    @Test
    public void testDisconnected () {
        getCluster().getNodes().add(getMockNode());

        when(getMockNode().isConnected()).thenReturn(false);
        boolean result = getCluster().disconnected();
        assert (result);

        when(getMockNode().isConnected()).thenReturn(true);
        result = getCluster().disconnected();
        assert (!result);
    }

    @Test
    public void testShutdown () {
        getCluster().getNodes().add(getMockNode());
        getCluster().shutdown();
        verify(getMockNode(), atLeastOnce()).sendShutdown(Matchers.any(BlockingQueue.class), Matchers.any());
    }

}
