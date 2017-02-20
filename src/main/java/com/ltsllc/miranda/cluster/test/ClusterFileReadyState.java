package com.ltsllc.miranda.cluster.test;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.*;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.node.GetVersionMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.TestCase;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */
public class ClusterFileReadyState extends TestCase {
    private com.ltsllc.miranda.cluster.ClusterFile clusterFile;
    private BlockingQueue<Message> cluster;

    private static final String CLUSTER_FILENAME = "cluster.json";

    private static final String[] CLUSTER_FILE_CONTENTS = {
            "[",
            "    {",
            "        \"dns\" : \"foo.com\",",
            "        \"ip\" : \"192.168.1.1\",",
            "        \"port\" : 6789,",
            "        \"description\" : \"a test node\",",
            "        \"expires\" : " + Long.MAX_VALUE,
            "    }",
            "]"
    };



    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    public BlockingQueue<Message> getCluster () {
        return cluster;
    }

    @Before
    public void setup () {
        deleteFile(CLUSTER_FILENAME);
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        ClusterFile.initialize(CLUSTER_FILENAME, getWriter(), getCluster());
        this.clusterFile = ClusterFile.getInstance();
        reset();
    }

    @After
    public void cleanup () {
        deleteFile(CLUSTER_FILENAME);
    }

    @Test
    public void testProcessMessageGetVersion () {
        BlockingQueue<Message> myQueue = new LinkedBlockingQueue<Message>();
        GetVersionMessage getVersionMessage = new GetVersionMessage(null, this, myQueue);
        send (getVersionMessage, getClusterFile().getQueue());

        pause(1000);

        assert(myQueue.size() == 1);
    }
}
