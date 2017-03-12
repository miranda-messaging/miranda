package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.messages.*;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.ltsllc.miranda.test.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ltsllc.miranda.miranda.Miranda.properties;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestClusterReadyState extends TestCase {
    private Cluster cluster;

    private static final String PROPERTIES_FILENAME = "junk.properties";

    public void reset() {
        super.reset();

        setupKeyStore();
        setupTrustStore();

        Cluster.reset();

        deleteFile(PROPERTIES_FILENAME);

        MirandaProperties properties = new MirandaProperties();
        String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);
        Cluster.initializeClass(filename, getWriter(), getMockNetwork());
        this.cluster = Cluster.getInstance();
    }

    @Before
    public void setup() {
        setuplog4j();
        reset();
    }

    public Cluster getCluster() {
        return cluster;
    }

    @After
    public void cleanup() {
        cleanupTrustStore();
    }

    @Test
    public void testProcessMessageLoad() {
        setupMirandaProperties();
        String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);
        LoadMessage message = new LoadMessage(null, filename, this);
        send(message, getCluster().getQueue());

        assert (getCluster().getClusterFileQueue() != null);
    }

    /**
     * When a cluster gets a {@link ConnectMessage}, it should drop everything
     * and connect.
     */
    @Test
    public void testProcessMessageConnect() {
        setupTrustStore();
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a ssltest node");
        Node node = new Node(nodeElement, getMockNetwork());
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(node);
        getCluster().replaceNodes (nodes);

        ConnectMessage message = new ConnectMessage(null, this, "localhost", 6789);
        send(message, getCluster().getQueue());

        pause(125);

        assert (contains(Message.Subjects.ConnectTo, getNetwork()));
    }

    /**
     * This gets sent when the cluster file reloads.  Make sure connects are issued
     * for the new node(s).  In this ssltest we add a new node.
     */
    @Test
    public void testProcessMessageNodesLoaded() {
        NodeElement node = new NodeElement("bar.com", "192.168.1.2", 6790, "a different ssltest node");
        List<NodeElement> nodes = new ArrayList<NodeElement>();
        nodes.add(node);

        NodesLoadedMessage message = new NodesLoadedMessage(nodes, null, this);
        send(message, getCluster().getQueue());

        pause(125);

        assert (contains(Message.Subjects.ConnectTo, getNetwork()));
    }


    /**
     * The requester of this message wants version information about the
     * {@link TestClusterFile}.  Ensure that we sendToMe a
     * {@link Message.Subjects#Version} message, and that
     * we sendToMe it to the right place.
     */
    @Test
    public void testProcessMessageGetVersion() {
        BlockingQueue<Message> myQueue = new LinkedBlockingQueue<Message>();
        BlockingQueue<Message> wrongQueue = new LinkedBlockingQueue<Message>();

        GetVersionMessage message = new GetVersionMessage(wrongQueue, this, myQueue);
        send(message, getCluster().getQueue());

        pause(125);

        assert (wrongQueue.size() == 0);
        assert (contains(Message.Subjects.Version, myQueue));
    }

    /**
     * This message gets sent because another node in the cluster has a different
     * cluster file, and merging the remote file with our local file yielded at
     * least one node we did not have.  This test ensures that we try to connect
     * to the new node.
     */
    @Test
    public void testProcessMessageCluserFileChanged() {
        NodeElement newNode = new NodeElement("bar.com", "192.168.1.2", 6790, "a different ssltest node");
        List<NodeElement> newNodes = new ArrayList<NodeElement>();
        newNodes.add(newNode);
        Version version = createVersion(newNodes);
        ClusterFileChangedMessage message = new ClusterFileChangedMessage(null, this, newNodes, version);
        send(message, getCluster().getQueue());

        pause(125);

        assert (contains(Message.Subjects.ConnectTo, getNetwork()));
    }

    /**
     * This message is sent when we should "garbage collect" our nodes.
     * The ssltest needs to enusure that a {@link HealthCheckUpdateMessage}
     * is sent to the cluster file.
     */
    @Test
    public void testProcessMessageHealthCheck() {
        BlockingQueue<Message> clusterFileQueue = new LinkedBlockingQueue<Message>();
        getCluster().replaceClusterFileQueue(clusterFileQueue);

        HealthCheckMessage message = new HealthCheckMessage(null, this);
        send(message, getCluster().getQueue());

        pause(125);

        assert(contains(Message.Subjects.HealthCheckUpdate, getCluster().getClusterFileQueue()));
    }

    /*

        @Test
    public void testProcessLoadMessage () {
        LoadMessage loadMessage = new LoadMessage(null, "whatever", this);
        getClusterReadyState().processMessage(loadMessage);

        verify(getMockCluster(), atLeastOnce()).getClusterFileQueue();
    }

    @Test
    public void testProcessConnectMessage () {
        ConnectMessage connectMessage = new ConnectMessage(null, this, "foo.com", 6789);

        verify(getMockCluster(), atLeastOnce()).connect();
    }

    @Test
    public void testProcessNodesLoaded () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        List<NodeElement> nodeList = new ArrayList<NodeElement>();
        nodeList.add (nodeElement);
        NodesLoadedMessage nodesLoadedMessage = new NodesLoadedMessage(nodeList, null, this);

        verify(getMockCluster(), atLeastOnce()).contains(eq(nodeElement));
    }

    @Test
    public void testProcessMessageGetVersion() {
        GetVersionsMessage getVersionsMessage = new GetVersionsMessage(null, this, null);
        getClusterReadyState().processMessage(getVersionsMessage);

        verify(getMockCluster(), atLeastOnce()).getClusterFileQueue();
    }

    @Test
    public void testProcessMessageNewNode() {
        NewNodeMessage newNodeMessage = new NewNodeMessage(null, this, getMockNode());
        getClusterReadyState().processMessage(newNodeMessage);
    }


    @Test
    public void testProcessMessageNodeUpdated() {
        NodeElement oldNode = new NodeElement("foo.com", "192.168.1.1", 6789, "a ssltest node");
        NodeElement newNode = new NodeElement("bar.com", "192.168.1.2", 6790, "a different ssltest node");
        NodeUpdatedMessage message = new NodeUpdatedMessage(null, this, oldNode, newNode);

        getClusterReadyState().processMessage(message);

        pause(500);

    }

    @Test
    public void testProcessClusterFileChanged () {
        List<NodeElement> nodeList = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        nodeList.add(nodeElement);

        Gson gson = new Gson();

        String json = gson.toJson(nodeList);
        Version version = new Version(json);

        ClusterFileChangedMessage message = new ClusterFileChangedMessage(null, this, nodeList, version);

        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).contains(eq(nodeElement));
    }

    @Test
    public void testProcessHealthCheckMessage() {
        HealthCheckMessage healthCheckMessage = new HealthCheckMessage(null, this);

        getClusterReadyState().processMessage(healthCheckMessage);

        verify(getMockCluster(), atLeastOnce()).performHealthCheck();
    }


    @Test
    public void testProcessNewNodeMessage () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        Node node = new Node (nodeElement, getMockNetwork());
        NewNodeMessage newNodeMessage = new NewNodeMessage(null, this, node);

        getClusterReadyState()

    }
    */


}
