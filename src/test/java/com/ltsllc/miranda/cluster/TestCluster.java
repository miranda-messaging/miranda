package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.property.MirandaProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.ltsllc.miranda.test.TestCase;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestCluster extends TestCase {
    private ClusterFile clusterFile;
    private Cluster cluster;


    public ClusterFile getClusterFile() {
        return clusterFile;
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


    @Before
    public void setup () {
        setuplog4j();
        setupMirandaProperties();
        setupMiranda();
        setupTimer();
        setupTrustStore();

        deleteFile(CLUSTER_FILENAME);
        Cluster.reset();

        MirandaProperties properties = Miranda.properties;
        String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);

        Cluster.initializeClass(filename, getWriter(), getMockNetwork());
        this.cluster = Cluster.getInstance();
        this.clusterFile = ClusterFile.getInstance();
    }

    @After
    public void cleanup () {
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
        com.ltsllc.miranda.cluster.Cluster.initializeClass(CLUSTER_FILENAME, getWriter(), getMockNetwork());
        this.cluster = com.ltsllc.miranda.cluster.Cluster.getInstance();

        ClusterFile temp = ClusterFile.getInstance();
        assert(null != temp);
        assert(getCluster().getClusterFileQueue() != null);
    }


    @Test
    public void testStart () {
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        getCluster().load();

        pause(50);

        Cluster cluster = Cluster.getInstance();
        Node node = cluster.getNodes().get(0);

        pause(125);

        getCluster().connect();

        pause(125);

        verify(getMockNetwork(), atLeastOnce()).sendConnect(eq(node.getQueue()), Matchers.any(), eq("foo.com"), eq(6789));
    }

    /**
     * Ensure that we create a load message.
     */
    @Test
    public void testLoad () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        getCluster().replaceClusterFileQueue(queue);

        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        // TODO: getCluster().load(CLUSTER_FILENAME);

        pause(125);

        assert (contains(Message.Subjects.Load, queue));
    }

    @Test
    public void testContains ()
    {
        // TODO : ssltest this
    }
}
