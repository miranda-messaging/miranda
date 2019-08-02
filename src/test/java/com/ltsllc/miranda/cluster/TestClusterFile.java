/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.*;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.cluster.states.ClusterFileStartingState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.test.TestCase;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestClusterFile extends TestCase {
    private static final String CLUSTER_FILENAME = "testClusterFile";

    private static Logger logger = Logger.getLogger(TestClusterFile.class);

    private ClusterFile clusterFile;

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

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
            "        \"description\" : \"a different ssltest node\",",
            "        \"expires\" : " + Long.MAX_VALUE,
            "    }",
            "]"
    };

    private BlockingQueue<Message> cluster = new LinkedBlockingQueue<Message>();

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    public void setupClusterFile() throws IOException, MirandaException {
        MirandaProperties properties = Miranda.properties;
        String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);

        deleteFile(filename);

        putFile(filename, CLUSTER_FILE_CONTENTS);

        ClusterFile.initialize(filename, getMockReader(), getMockWriter(), getCluster());
        this.clusterFile = ClusterFile.getInstance();

        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a test node");
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        nodeElementList.add(nodeElement);
        this.clusterFile.setData(nodeElementList);
    }

    public void reset () throws Exception {
        super.reset();

        Miranda.properties = null;
        ClusterFile.reset();
    }


    @Before
    public void setup() throws Exception {
        reset();

        super.setup();

        setuplog4j();
        setupWriter();
        setupTimer();
        setupMiranda();
        setupMirandaProperties();
        setupMockReader();

        try {
            setupClusterFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanup() {
        deleteFile(CLUSTER_FILENAME);
    }

    @Test
    public void testInitialize() {
        NodeElement nodeElement = new NodeElement("foo.com",6789, "a ssltest node");
        getClusterFile().add(nodeElement);
        assert (getClusterFile().contains(nodeElement));
    }

    @Test
    public void testConstructor () {
        assert (getClusterFile().getCurrentState() instanceof ClusterFileStartingState);
    }

    @Test
    public void testLoad() {
        assert (null != com.ltsllc.miranda.cluster.ClusterFile.getInstance());

        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS2);

        LoadMessage loadMessage = new LoadMessage(null, this);
        Consumer.staticSend(loadMessage, com.ltsllc.miranda.cluster.ClusterFile.getInstance().getQueue());

        pause(125);

        NodeElement nodeElement = new NodeElement("bar.com",6790, "a different ssltest node");
        com.ltsllc.miranda.cluster.ClusterFile.getInstance().contains(nodeElement);
    }

    @Test
    public void testUpdateNode() {
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a ssltest node");
        long now = System.currentTimeMillis();
        nodeElement.setLastConnected(now);
        com.ltsllc.miranda.cluster.ClusterFile.getInstance().updateNode(nodeElement);
        List<NodeElement> nodes = com.ltsllc.miranda.cluster.ClusterFile.getInstance().getData();
        for (NodeElement element : nodes) {
            if (element.equals(nodeElement)) {
                assert (element.getLastConnected() == now);
            }
        }
    }

    @Test
    public void testAdd() {
        NodeElement nodeElement = new NodeElement("bar.com", 6790, "a different ssltest node");
        getClusterFile().addNode(nodeElement);

        verify(getMockWriter(), atLeastOnce()).sendWrite(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyString(), Matchers.any(byte[].class));
    }

    @Test
    public void testContainsNode () {
        NodeElement contains = new NodeElement("foo.com", 6789, "a test node");
        NodeElement doesNotContain = new NodeElement("bar.com", 6789, "a different test node");

        assert(getClusterFile().contains(contains));
        assert(!getClusterFile().contains(doesNotContain));
    }

    @Test
    public void testContainsElement () {
        NodeElement contains = new NodeElement("foo.com", 6789, "a test node");
        NodeElement doesNotContain = new NodeElement("bar.com", 6789, "a different test node");

        assert(getClusterFile().containsElement(contains));
        assert(!getClusterFile().containsElement(doesNotContain));
    }

    @Test
    public void testMergeNewElement () {
        NodeElement newElement = new NodeElement("bar.com", 6789, "a new element");
        List<NodeElement> newElementList = new ArrayList<NodeElement>();
        newElementList.add(newElement);

        List<Subscription> subscriptionList = null;

        Version oldVersion = getClusterFile().getVersion();

        getClusterFile().merge(newElementList);

        Version newVersion = getClusterFile().getVersion();

        assert (getClusterFile().contains(newElement));
        assert (oldVersion != newVersion);
        verify(getMockWriter()).sendWrite(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyString(), Matchers.any(byte[].class));
    }

    @Test
    public void testMergeNoChange () {
        Version oldVersion = getClusterFile().getVersion();

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        when(getMockWriter().getQueue()).thenReturn(queue);


        Version newVersion = getClusterFile().getVersion();
        assert (oldVersion.equals(newVersion));
        assert (!contains(Message.Subjects.Write, queue));
        assert (!contains(Message.Subjects.ClusterFileChanged, getCluster()));
    }

    @Test
    public void testMatchingNode () {
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a test node");

        NodeElement match = getClusterFile().matchingNode(nodeElement);

        assert (match != null);
        assert (match.equals(nodeElement));
    }

    @Test
    public void testCheckForDuplicatesHasDuplicates () {
        getClusterFile().setLogger(getMockLogger());
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a test node");

        getClusterFile().getData().add (nodeElement);

        getClusterFile().checkForDuplicates();

        verify(getMockLogger(), atLeastOnce()).warn(Matchers.anyString());
    }

    @Test
    public void testCheckForDuplicatesNoDuplicates () {
        getClusterFile().checkForDuplicates();

        verify(getMockLogger(), never()).warn(Matchers.anyString());
    }
}
