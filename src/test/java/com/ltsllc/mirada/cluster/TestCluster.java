package com.ltsllc.mirada.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.file.MirandaProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ssltest.TestCase;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestCluster extends TestCase {
    private ClusterFile clusterFile;
    private com.ltsllc.miranda.cluster.Cluster cluster;

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
        setupTrustStore();
        deleteFile(CLUSTER_FILENAME);
        com.ltsllc.miranda.cluster.Cluster.reset();

        MirandaProperties properties = MirandaProperties.getInstance();
        String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);

        com.ltsllc.miranda.cluster.Cluster.initializeClass(filename, getWriter(), getNetwork());
        this.cluster = com.ltsllc.miranda.cluster.Cluster.getInstance();
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
        com.ltsllc.miranda.cluster.Cluster.initializeClass(CLUSTER_FILENAME, getWriter(), getNetwork());
        this.cluster = com.ltsllc.miranda.cluster.Cluster.getInstance();

        ClusterFile temp = ClusterFile.getInstance();
        assert(null != temp);
        assert(getCluster().getClusterFileQueue() != null);
    }


    @Test
    public void testStart () {
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        com.ltsllc.miranda.cluster.Cluster.initializeClass(CLUSTER_FILENAME, getWriter(), getNetwork());
        getCluster().load(CLUSTER_FILENAME);

        pause(125);

        getCluster().connect();

        pause(125);

        assert(contains(Message.Subjects.ConnectTo, getNetwork()));
    }

    /**
     * Ensure that we create a load message.
     */
    @Test
    public void testLoad () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        getCluster().replaceClusterFileQueue(queue);

        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        getCluster().load(CLUSTER_FILENAME);

        pause(125);

        assert (contains(Message.Subjects.Load, queue));
    }

    @Test
    public void testContains ()
    {
        // TODO : ssltest this
    }
}
