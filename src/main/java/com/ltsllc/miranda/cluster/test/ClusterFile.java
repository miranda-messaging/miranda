package com.ltsllc.miranda.cluster.test;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.LoadMessage;
import com.ltsllc.miranda.node.NodeElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.TestCase;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */
public class ClusterFile extends TestCase{
    private static final String CLUSTER_FILENAME = "testClusterFile";

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

    private static final String[] CLUSTER_FILE_CONTENTS2 = {
            "[",
            "    {",
            "        \"dns\" : \"bar.com\",",
            "        \"ip\" : \"192.168.1.2\",",
            "        \"port\" : 6790,",
            "        \"description\" : \"a different test node\",",
            "        \"expires\" : " + Long.MAX_VALUE,
            "    }",
            "]"
    };

    private BlockingQueue<Message> cluster = new LinkedBlockingQueue<Message>();

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    @Before
    public void setup () {
        deleteFile(CLUSTER_FILENAME);
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
    }

    @After
    public void cleanup () {
        deleteFile(CLUSTER_FILENAME);
    }

    @Test
    public void testInitialize () {
        com.ltsllc.miranda.cluster.ClusterFile.initialize(CLUSTER_FILENAME, getWriter(), getCluster());

        NodeElement nodeElement = new NodeElement("bar.com", "192.168.1.2", 6790, "a different test node");
        assert (com.ltsllc.miranda.cluster.ClusterFile.getInstance().contains(nodeElement));

        assert (com.ltsllc.miranda.cluster.ClusterFile.getInstance().getCurrentState().toString().equals("ReadyState"));
    }

    @Test
    public void testLoad () {
        com.ltsllc.miranda.cluster.ClusterFile.initialize(CLUSTER_FILENAME, getWriter(), getCluster());

        assert(null != com.ltsllc.miranda.cluster.ClusterFile.getInstance());

        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS2);

        LoadMessage loadMessage = new LoadMessage(null, CLUSTER_FILENAME, this);
        Consumer.staticSend(loadMessage, com.ltsllc.miranda.cluster.ClusterFile.getInstance().getQueue());

        pause(1000);

        NodeElement nodeElement = new NodeElement("bar.com", "192.168.1.2", 6790, "a different test node");
        com.ltsllc.miranda.cluster.ClusterFile.getInstance().contains(nodeElement);
    }

    @Test
    public void testUpdateNode () {
        com.ltsllc.miranda.cluster.ClusterFile.initialize(CLUSTER_FILENAME, getWriter(), getCluster());

        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.2",6789, "a test node");
        long now = System.currentTimeMillis();
        nodeElement.setLastConnected(now);
        com.ltsllc.miranda.cluster.ClusterFile.getInstance().updateNode(nodeElement);
        List<NodeElement> nodes = com.ltsllc.miranda.cluster.ClusterFile.getInstance().getData();
        for (NodeElement element : nodes) {
            if (element.equals(nodeElement))
            {
                assert(element.getLastConnected() == now);
            }
        }



    }
}
