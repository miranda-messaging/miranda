package com.ltsllc.mirada;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.HealthCheckUpdateMessage;
import com.ltsllc.miranda.cluster.NewClusterFileMessage;
import com.ltsllc.miranda.node.GetClusterFileMessage;
import com.ltsllc.miranda.node.GetVersionMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.NodeUpdatedMessage;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestClusterFileReadyState extends TestCase {
    private static Logger logger = Logger.getLogger(TestClusterFileReadyState.class);
    private static Gson ourGson = new Gson();

    private ClusterFile clusterFile;
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

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    public void reset() {
        super.reset();

        ClusterFile.reset();
        this.cluster = new LinkedBlockingQueue<Message>();
    }


    private static final String MIRANADA_PROPERTIES_FILENAME = "junk.properties";


    @Before
    public void setup() {
        reset();
        deleteFile(CLUSTER_FILENAME);
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);
        ClusterFile.initialize(CLUSTER_FILENAME, getWriter(), getCluster());
        this.clusterFile = ClusterFile.getInstance();
    }

    @After
    public void cleanup() {
        deleteFile(CLUSTER_FILENAME);
        reset();
    }

    @Test
    public void testProcessMessageGetVersion() {
        BlockingQueue<Message> myQueue = new LinkedBlockingQueue<Message>();
        GetVersionMessage getVersionMessage = new GetVersionMessage(null, this, myQueue);
        send(getVersionMessage, getClusterFile().getQueue());

        pause(125);

        assert (myQueue.size() == 1);
        assert (contains(Message.Subjects.Version, myQueue));
    }

    @Test
    public void testProcessMessageNewNode() {
        BlockingQueue<Message> myQueue = new LinkedBlockingQueue<Message>();
        Version oldVersion = getClusterFile().getVersion();

        NodeElement nodeElement = new NodeElement("bar.com", "192.168.1.2", 6789, "a different test node");
        getClusterFile().add(nodeElement);
        Version newVersion = getClusterFile().getVersion();

        assert (getClusterFile().contains(nodeElement));
        assert (oldVersion != newVersion);
        assert (!oldVersion.equals(newVersion));
    }

    @Test
    public void testProcessMessageWriteSucceeded() {
        WriteSucceededMessage message = new WriteSucceededMessage(null, getClusterFile().getFilename(), this);
        send(message, getClusterFile().getQueue());

        //
        // this message should be ignored
        //
        assert (0 == getCluster().size());
        assert (0 == getWriter().size());
    }

    @Test
    public void testProcessMessageWriteFailed() {
        setuplog4j();
        WriteFailedMessage message = new WriteFailedMessage(null, getClusterFile().getFilename(), null, this);
        send(message, getClusterFile().getQueue());

        pause(125);
    }


    @Test
    public void testProcessMessageNodeUpdated() {
        NodeElement oldNode = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        NodeElement newNode = new NodeElement("bar.com", "192.168.1.2", 6790, "a different test node");
        NodeUpdatedMessage message = new NodeUpdatedMessage(null, this, oldNode, newNode);
        send(message, getClusterFile().getQueue());

        pause(500);

        assert (getClusterFile().contains(newNode));
        assert (!getClusterFile().contains(oldNode));
        assert (contains(Message.Subjects.Write, getWriter()));
    }

    @Test
    public void testProcessMessageGetClusterFile() {
        BlockingQueue<Message> myQueue = new LinkedBlockingQueue<Message>();

        GetClusterFileMessage message = new GetClusterFileMessage(myQueue, this);
        send(message, getClusterFile().getQueue());

        pause(500);

        assert (contains(Message.Subjects.ClusterFile, myQueue));
    }

    /**
     * This happens when we connect to a cluster, and it has a different version
     * of the cluster file.  In this case we should merge the cluster's file
     * with our local copy.
     */
    @Test
    public void testProcessMessageNewClusterFile() {
        List<NodeElement> file = new ArrayList<NodeElement>();
        NodeElement newNode = new NodeElement("bar.com", "192.168.1.2", 6790, "a diffent test node");
        file.add(newNode);
        String json = ourGson.toJson(file);
        Version junk = new Version(json);

        NewClusterFileMessage message = new NewClusterFileMessage(null, this, file, junk);
        send(message, getClusterFile().getQueue());

        //
        // yield the prcessor
        //
        pause(250);

        NodeElement oldNode = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");

        assert (getClusterFile().contains(oldNode));
        assert (getClusterFile().contains(newNode));
    }

    /**
     * A {@link HealthCheckUpdateMessage} signifies that
     * the specifed nodes are connected, and should have their times of last
     * connection updated to now.
     */
    @Test
    public void testProcessMessageHealthCheckUpdate() {
        //
        // MirandaProperties are used to determine if a node should be dropped
        //
        setupMirandaProperties();

        for (NodeElement element : getClusterFile().getData()) {
            element.setLastConnected(0);
        }

        //
        // update some nodes
        //
        NodeElement node = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        List<NodeElement> updates = new ArrayList<NodeElement>();
        updates.add(node);
        HealthCheckUpdateMessage message = new HealthCheckUpdateMessage(null, this, updates);
        send(message, getClusterFile().getQueue());

        //
        // yield the processor
        //
        pause(250);

        //
        // if everything worked, then all the nodes should have updated
        //
        boolean updated = true;

        for (NodeElement element : getClusterFile().getData()) {
            if (element.getLastConnected() <= 0) {
                updated = false;
            }
        }

        assert (updated);
        assert (contains(Message.Subjects.Write, getWriter()));
    }

    @Test
    public void testProcessMessageHealthCheckUpdateDropNode() {
        //
        // MirandaProperties are used to determine if a node should be dropped
        //
        setupMirandaProperties();

        NodeElement oldNode = new NodeElement("oldfoo.com", "192.168.1.3", 6791, "a node that hasn't reconnected");
        getClusterFile().getData().add(oldNode);

        //
        // set the last connected time to 0 for all nodes
        //
        for (NodeElement element : getClusterFile().getData()) {
            element.setLastConnected(0);
        }

        NodeElement node = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        List<NodeElement> updates = new ArrayList<NodeElement>();
        updates.add(node);
        HealthCheckUpdateMessage message = new HealthCheckUpdateMessage(null, this, updates);
        send(message, getClusterFile().getQueue());

        //
        // yield the processor
        //
        pause(250);

        //
        // old node should have been dropped
        //
        assert (!getClusterFile().contains(oldNode));
        assert (contains(Message.Subjects.DropNode, getCluster()));
    }
}