package com.ltsllc.miranda.cluster.test;

import com.ltsllc.miranda.node.NodeElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.TestCase;

/**
 * Created by Clark on 2/20/2017.
 */
public class Cluster extends TestCase {
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

    @Before
    public void setup () {
        deleteFile(CLUSTER_FILENAME);
        com.ltsllc.miranda.cluster.Cluster.reset();
    }

    @After
    public void cleanup () {
        deleteFile(CLUSTER_FILENAME);
    }

    @Test
    public void testInitialize() {
        assert (null == com.ltsllc.miranda.cluster.Cluster.getInstance());
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        com.ltsllc.miranda.cluster.Cluster.initializeClass(CLUSTER_FILENAME, getWriter(), getNetwork());
        assert (0 == getNetwork().size());
        assert (0 == getWriter().size());

        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        assert (com.ltsllc.miranda.cluster.Cluster.getInstance().getClusterFile().contains(nodeElement));
    }


    @Test
    public void testStart () {
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        com.ltsllc.miranda.cluster.Cluster.initializeClass(CLUSTER_FILENAME, getWriter(), getNetwork());
        com.ltsllc.miranda.cluster.Cluster.getInstance().connect();

        assert(getNetwork().size() == 1);
    }

    @Test
    public void testLoad () {
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        com.ltsllc.miranda.cluster.Cluster.initializeClass(CLUSTER_FILENAME, getWriter(), getNetwork());

        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        assert(com.ltsllc.miranda.cluster.Cluster.getInstance().getClusterFile().contains(nodeElement));
    }

    @Test
    public void testContains ()
    {
        // TODO : test this
    }
}
