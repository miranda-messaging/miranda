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
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.cluster.messages.NodesUpdatedMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.test.TestCase;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
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
        Version version = new Version();
        version.setSha256("foo");
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetVersionMessage message = new GetVersionMessage(queue, null, queue);

        when(getMockClusterfile().getVersion()).thenReturn(version);

        getClusterFileReadyState().processMessage(message);

        assert(contains(Message.Subjects.Version, queue));
    }

    /**
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

    /**
     * This is called when someone else in the cluster wants a copy of our
     * cluster file.
     */
    @Test
    public void testProcessGetClusterFileMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetClusterFileMessage message = new GetClusterFileMessage(queue, this);

        getClusterFileReadyState().processMessage(message);

        verify(getMockClusterfile(), atLeastOnce()).getData();
        verify(getMockClusterfile(), atLeastOnce()).getVersion();
        assert(contains(Message.Subjects.ClusterFile, queue));
    }
}