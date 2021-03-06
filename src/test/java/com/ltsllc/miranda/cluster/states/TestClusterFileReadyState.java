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

package com.ltsllc.miranda.cluster.states;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.HealthCheckUpdateMessage;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.cluster.messages.NodesUpdatedMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.test.TestCase;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestClusterFileReadyState extends TestCase {
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

    private static Logger logger = Logger.getLogger(TestClusterFileReadyState.class);
    private static Gson ourGson = new Gson();

    private ClusterFileReadyState clusterFileReadyState;

    @Mock
    private ClusterFile mockClusterfile = mock(ClusterFile.class);

    @Mock
    private Logger mockLogger = mock(Logger.class);

    public TestClusterFileReadyState () {
    }


    public ClusterFileReadyState getClusterFileReadyState() {
        return clusterFileReadyState;
    }

    public ClusterFile getMockClusterfile() {
        return mockClusterfile;
    }

    public void reset() throws Exception {
        super.reset();

        ClusterFile.reset();
        this.mockClusterfile = mock(ClusterFile.class);
        this.mockLogger = mock(Logger.class);
    }


    private static final String MIRANADA_PROPERTIES_FILENAME = "junk.properties";

    public Logger getMockLogger() {
        return mockLogger;
    }

    public void setupMockLogger () {
        getClusterFileReadyState().setLogger(getMockLogger());
    }

    @Before
    public void setup() throws Exception {
        reset();

        super.setup();

        setuplog4j();
        setupMirandaProperties();
        setupTimer();
        deleteFile(CLUSTER_FILENAME);
        putFile(CLUSTER_FILENAME, CLUSTER_FILE_CONTENTS);

        this.clusterFileReadyState = new ClusterFileReadyState(getMockClusterfile());
    }

    @After
    public void cleanup() throws Exception {
        deleteFile(CLUSTER_FILENAME);
        reset();
    }

    @Test
    public void testProcessLoadMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        LoadMessage loadMessage = new LoadMessage (queue, this);

        when(getClusterFileReadyState().getFile().getReader()).thenReturn(getMockReader());
        getClusterFileReadyState().processMessage(loadMessage);

        assert(contains(Message.Subjects.LoadResponse, queue));
    }

    @Test
    public void testProcessGetVersionMessage () throws MirandaException {
        try {
            setupMockCluster();
            setupMockMiranda();
            Miranda miranda = Miranda.getInstance();
            Map<Files, Version> fileToVersion = new HashMap<>();
            setupMockCluster();
            setupMockTopicsManager();
            setupMockUserManager();
            setupMockDeliveryManager();
            setupMockEventManager();


            when(miranda.getTopicManager()).thenReturn(getMockTopicManager());
            when(miranda.getUserManager()).thenReturn(getMockUserManager());
            when (miranda.getCluster()).thenReturn(getMockCluster());

            when (miranda.getSubscriptionManager()).thenReturn(getMockSubscriptionManager());
            when (miranda.getDeliveryManager()).thenReturn(getMockDeliveryManager());
            when (miranda.getEventManager()).thenReturn(getMockEventManager());
            when (miranda.getDeliveryManager()).thenReturn(getMockDeliveryManager());
            when (getMockCluster().getLocalNode()).thenReturn(getMockNode());
            setupMockNode();

            fileToVersion.put(Files.Topic, miranda.getTopicManager().getVersion());
            fileToVersion.put(Files.Subscription, miranda.getSubscriptionManager().getVersion());
            fileToVersion.put(Files.User, miranda.getUserManager().getVersion());
            fileToVersion.put(Files.Cluster, miranda.getCluster().getVersion());
            fileToVersion.put(Files.DeliveriesList, miranda.getDeliveryManager().getVersion());
            fileToVersion.put (Files.EventList,  miranda.getEventManager().getVersion());

            setupMockNode();
            Version version = new Version();
            version.setSha256("foo");
            BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
            GetVersionMessage message = new GetVersionMessage(queue, null, queue);

            when(getMockClusterfile().getVersion()).thenReturn(version);
            when(getMockCluster().getQueue()).thenReturn(new LinkedBlockingQueue<>());
            when(getMockClusterfile().getQueue()).thenReturn(new LinkedBlockingQueue<>());

            getClusterFileReadyState().processMessage(message);

            assert (contains(Message.Subjects.Version, queue));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);

        }
    }



    /**
     * The cluster tells the cluster file it has updated the nodes
     */
    @Test
    public void testProcessNodesUpdatedMessage () throws MirandaException {
        List<NodeElement> nodeList = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a node");
        nodeList.add(nodeElement);

        NodesUpdatedMessage message = new NodesUpdatedMessage(null, this, nodeList);

        getClusterFileReadyState().processMessage(message);

        verify(getMockClusterfile(), atLeastOnce()).setData(eq(nodeList));
        verify(getMockClusterfile(), atLeastOnce()).write();
    }

    @Test
    public void testProcessHealthCheckUpdateMessage () throws MirandaException {
        NodeElement nodeElement = new NodeElement("test.com", 6789,"A test node");
        List list = new ArrayList();
        list.add(nodeElement);
        HealthCheckUpdateMessage healthCheckUpdateMessage= new HealthCheckUpdateMessage(null, null,
                list);

        when(getMockClusterfile().matchingNode(any())).thenReturn(nodeElement);

        getClusterFileReadyState().processMessage(healthCheckUpdateMessage);

        verify(getMockClusterfile(), atLeastOnce()).updateVersion();
        verify (getMockClusterfile(), atLeastOnce()).write();

        //
        // verify that drops work
        //
        NodeElement timedOut = new NodeElement("timed.out", 6789, "a node that has timed out");
        List<NodeElement> nodeElementList = new ArrayList<>();
        nodeElementList.add(timedOut);

        when(getMockClusterfile().getData()).thenReturn(nodeElementList);
        HealthCheckUpdateMessage healthCheckUpdateMessage2 = new HealthCheckUpdateMessage(null,null,
                new ArrayList<>());

        when(getMockClusterfile().getData()).thenReturn(nodeElementList);

        assert(!getMockClusterfile().containsElement(timedOut));
    }

    /**
     * This is called when someone else in the cluster wants a copy of our
     * cluster file.
     */
    public void testProcessGetClusterFileMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetClusterFileMessage message = new GetClusterFileMessage(queue, this);

        getClusterFileReadyState().processMessage(message);

        verify(getMockClusterfile(), atLeastOnce()).getData();
        verify(getMockClusterfile(), atLeastOnce()).getVersion();
        assert(contains(Message.Subjects.ClusterFile, queue));
    }
}