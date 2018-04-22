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

package com.ltsllc.miranda.node;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.clientinterface.objects.NodeStatus;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.node.states.ConnectingState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/20/2017.
 */
public class TestNode extends TestCase {
    private Node node;

    public Node getNode() {
        return node;
    }

    public void reset () throws Exception {
        super.reset();

        node = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();

        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a node");
        node = new Node(nodeElement, getMockNetwork(), getMockCluster());
    }

    @Test
    public void testConstructor () {
        assert (getNode().getCurrentState() instanceof ConnectingState);
        assert (getNode().getDns().equals("foo.com"));
        assert (getNode().getPort() == 6789);
        assert (getNode().getDescription().equals("a node"));
    }

    @Test
    public void testOtherConstructor () throws MirandaException {
        this.node = new Node (1, getMockNetwork(), getMockCluster());

        assert (getNode().getHandle() == 1);
        assert (getNode().getNetwork() == getMockNetwork());
        assert (getNode().getCluster() == getMockCluster());
    }

    @Test
    public void testMatches () {
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a node");

        assert (getNode().matches(nodeElement));

        NodeElement nodeElement2 = new NodeElement("bar.com", 6789, "a node");
        assert (!getNode().matches(nodeElement2));

        nodeElement2 = new NodeElement("foo.com", 6790, "a node");
        assert (!getNode().matches(nodeElement2));

        nodeElement2 = new NodeElement("foo.com", 6789, "another node");
        assert (getNode().matches(nodeElement2));
    }

    @Test
    public void testAsNodeElement () {
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a node");

        NodeElement nodeElement2 = getNode().asNodeElement();

        assert (nodeElement.equals(nodeElement2));
    }

    @Test
    public void testGetStatus () {
        NodeStatus status = getNode().getStatus();

        assert (status.getLastConnected() == -1);
        assert (status.getDns().equals("foo.com"));
        assert (status.getPort() == 6789);
        assert (status.getDescription().equals("a node"));
    }

    @Test
    public void testStop () {
        setupMockNetwork();

        getNode().stop();

        verify(getMockNetwork(), atLeastOnce()).sendNetworkMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyInt(), Matchers.any(WireMessage.class));
    }
}
