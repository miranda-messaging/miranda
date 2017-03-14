package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.messages.*;
import com.ltsllc.miranda.miranda.GetVersionsMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.ltsllc.miranda.test.TestCase;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ltsllc.miranda.miranda.Miranda.properties;
import static org.mockito.Mockito.*;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestClusterReadyState extends TestCase {
    private static final String PROPERTIES_FILENAME = "junk.properties";

    @Mock
    private Cluster mockCluster;

    private ClusterReadyState clusterReadyState;

    @Override
    public Cluster getMockCluster() {
        return mockCluster;
    }

    public ClusterReadyState getClusterReadyState() {
        return clusterReadyState;
    }

    public void reset() {
        super.reset();

        this.mockCluster = null;

        deleteFile(PROPERTIES_FILENAME);
    }

    @Before
    public void setup() {
        reset();

        super.setup();

        setuplog4j();
        setupKeyStore();
        setupTrustStore();

        this.mockCluster = mock(Cluster.class);
        this.clusterReadyState = new ClusterReadyState(getMockCluster());
    }

    @After
    public void cleanup() {
        cleanupTrustStore();
    }

    @Test
    public void testProcessMessageLoad() {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        LoadMessage message = new LoadMessage(queue, "whatever", this);

        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).load();
    }

    /**
     * When a cluster gets a {@link ConnectMessage}, it should drop everything
     * and connect.
     */
    @Test
    public void testProcessMessageConnect() {
        ConnectMessage message = new ConnectMessage(null, this);

        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).connect();
    }

    /**
     * This gets sent when the cluster file reloads.  Make sure connects are issued
     * for the new node(s).  In this ssltest we add a new node.
     */
    @Test
    public void testProcessMessageNodesLoaded() {
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a naode");
        nodeElementList.add(nodeElement);
        NodesLoadedMessage message = new NodesLoadedMessage(nodeElementList, null, this);


        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).merge(Matchers.anyList());
    }


    /**
     * The requester of this message wants version information about the
     * {@link TestClusterFile}.  Ensure that we sendToMe a
     * {@link Message.Subjects#Version} message, and that
     * we sendToMe it to the right place.
     */
    @Test
    public void testProcessMessageGetVersion() {
        GetVersionMessage message = new GetVersionMessage(null, this, null);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        when(getMockCluster().getClusterFileQueue()).thenReturn(queue);

        getClusterReadyState().processMessage(message);

        assert (contains(Message.Subjects.GetVersion, queue));
    }


    /**
     * This message is sent when we should "garbage collect" our nodes.
     * The ssltest needs to enusure that a {@link HealthCheckUpdateMessage}
     * is sent to the cluster file.
     */
    @Test
    public void testProcessMessageHealthCheck() {
        HealthCheckMessage message = new HealthCheckMessage(null, this);

        getClusterReadyState().processMessage(message);

        verify(getMockCluster(), atLeastOnce()).performHealthCheck();
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
