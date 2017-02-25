package com.ltsllc.miranda.cluster;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.messages.HealthCheckUpdateMessage;
import com.ltsllc.miranda.cluster.messages.NewClusterFileMessage;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.ltsllc.miranda.test.TestCase;

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
            "        \"description\" : \"a ssltest node\",",
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

        NodeElement nodeElement = new NodeElement("bar.com", "192.168.1.2", 6789, "a different ssltest node");
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
        NodeElement oldNode = new NodeElement("foo.com", "192.168.1.1", 6789, "a ssltest node");
        NodeElement newNode = new NodeElement("bar.com", "192.168.1.2", 6790, "a different ssltest node");
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
        NodeElement newNode = new NodeElement("bar.com", "192.168.1.2", 6790, "a diffent ssltest node");
        file.add(newNode);
        String json = ourGson.toJson(file);
        Version junk = new Version(json);

        NewClusterFileMessage message = new NewClusterFileMessage(null, this, file, junk);
        send(message, getClusterFile().getQueue());

        //
        // yield the prcessor
        //
        pause(250);

        NodeElement oldNode = new NodeElement("foo.com", "192.168.1.1", 6789, "a ssltest node");

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
        NodeElement node = new NodeElement("foo.com", "192.168.1.1", 6789, "a ssltest node");
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

        NodeElement node = new NodeElement("foo.com", "192.168.1.1", 6789, "a ssltest node");
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

    /**
     * This is used by the superclass, when synchronizing with a remote file.
     * It basically just sends a write message on to the file.
     */
    @Test
    public void testWrite() {
        ClusterFileReadyState clusterFileReadyState = (ClusterFileReadyState) getClusterFile().getCurrentState();
        clusterFileReadyState.write();

        pause(125);

        assert (contains(Message.Subjects.Write, getWriter()));
    }

    /**
     * Contains is used by many other methods; just test that it is at least
     * minimally working.
     */
    @Test
    public void testContains() {
        NodeElement present = new NodeElement("foo.com", "192.168.1.1", 6789, "a test node");
        NodeElement absent = new NodeElement("bar.com", "192.168.1.2", 6790, "a diffenet test node");

        ClusterFileReadyState clusterFileReadyState = (ClusterFileReadyState) getClusterFile().getCurrentState();

        assert (clusterFileReadyState.contains(present));
        assert (!clusterFileReadyState.contains(absent));
    }

    @Test
    public void testGetVersion() {
        ClusterFileReadyState clusterFileReadyState = (ClusterFileReadyState) getClusterFile().getCurrentState();
        Version cfrsVersion = clusterFileReadyState.getVersion();

        Version cfVersion = getClusterFile().getVersion();

        assert (cfrsVersion.equals(cfrsVersion));
    }
}