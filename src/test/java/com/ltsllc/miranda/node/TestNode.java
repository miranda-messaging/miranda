package com.ltsllc.miranda.node;

import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.node.states.ConnectingState;
import com.ltsllc.miranda.servlet.objects.NodeStatus;
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

    public void reset () {
        super.reset();

        node = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        node = new Node(nodeElement, getMockNetwork(), getMockCluster());
    }

    @Test
    public void testConstructor () {
        assert (getNode().getCurrentState() instanceof ConnectingState);
        assert (getNode().getDns().equals("foo.com"));
        assert (getNode().getIp().equals("192.168.1.1"));
        assert (getNode().getPort() == 6789);
        assert (getNode().getDescription().equals("a node"));
    }

    @Test
    public void testOtherConstructor () {
        this.node = new Node (1, getMockNetwork(), getMockCluster());

        assert (getNode().getHandle() == 1);
        assert (getNode().getNetwork() == getMockNetwork());
        assert (getNode().getCluster() == getMockCluster());
    }

    @Test
    public void testMatches () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");

        assert (getNode().matches(nodeElement));

        NodeElement nodeElement2 = new NodeElement("bar.com", "192.168.1.1", 6789, "a node");
        assert (!getNode().matches(nodeElement2));

        nodeElement2 = new NodeElement("foo.com", "192.168.1.2", 6789, "a node");
        assert (!getNode().matches(nodeElement2));

        nodeElement2 = new NodeElement("foo.com", "192.168.1.1", 6790, "a node");
        assert (!getNode().matches(nodeElement2));

        nodeElement2 = new NodeElement("foo.com", "192.168.1.1", 6789, "another node");
        assert (getNode().matches(nodeElement2));
    }

    @Test
    public void testAsNodeElement () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");

        NodeElement nodeElement2 = getNode().asNodeElement();

        assert (nodeElement.equals(nodeElement2));
    }

    @Test
    public void testGetStatus () {
        NodeStatus status = getNode().getStatus();

        assert (status.getLastConnected() == -1);
        assert (status.getDns().equals("foo.com"));
        assert (status.getIp().equals("192.168.1.1"));
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
