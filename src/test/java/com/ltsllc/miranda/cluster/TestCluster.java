package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.writer.Writer;
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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestCluster extends TestCase {
    private Cluster cluster;

    @Mock
    private ClusterFile mockClusterFile;

    @Mock
    private Node mockNode;

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


    public void reset () {
        super.reset();

        this.cluster = null;
        this.mockClusterFile = null;
        this.mockNode = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupWriter();
        setupMirandaProperties();
        setupMiranda();
        setupTimer();
        setupMirandaProperties();
        setupTrustStore();

        deleteFile(CLUSTER_FILENAME);
        Cluster.reset();

        MirandaProperties properties = Miranda.properties;
        String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);

        this.mockClusterFile = mock(ClusterFile.class);
        this.mockNode = mock(Node.class);

        Cluster.initializeClass(filename, getMockWriter(), getMockNetwork());
        this.cluster = Cluster.getInstance();
    }

    @After
    public void cleanup () {
        cleanupTrustStore();
        deleteFile(CLUSTER_FILENAME);
    }

    /**
     * Ensure that {@link com.ltsllc.miranda.cluster.Cluster} created a
     * {@link ClusterFile}.
     */
    @Test
    public void testInitialize() {
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        com.ltsllc.miranda.cluster.Cluster.reset();
        com.ltsllc.miranda.cluster.Cluster.initializeClass(CLUSTER_FILENAME, Writer.getInstance(), getMockNetwork());
        this.cluster = com.ltsllc.miranda.cluster.Cluster.getInstance();

        ClusterFile temp = ClusterFile.getInstance();
        assert(null != temp);
        assert(getCluster().getClusterFileQueue() != null);
    }


    /**
     * Ensure that we create a load message.
     */
    @Test
    public void testLoad () {
        getCluster().setClusterFile(getMockClusterFile());

        getCluster().load();

        verify(getMockClusterFile(), atLeastOnce()).sendLoad(Matchers.any(BlockingQueue.class), Matchers.any());
    }

    @Test
    public void testContains ()
    {
        NodeElement shouldContain = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        Node node = new Node(shouldContain, getMockNetwork(), getMockCluster());
        NodeElement shouldNotContain = new NodeElement("bar.com", "192.168.1.2", 6790, "another node");
        List<Node> nodeList = new ArrayList<Node>(1);
        nodeList.add(node);

        getCluster().setNodes(nodeList);

        assert (getCluster().contains(shouldContain));
        assert (!getCluster().contains(shouldNotContain));
    }

    @Test
    public void testConnect () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");

    }
}
