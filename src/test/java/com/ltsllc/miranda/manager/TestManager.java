package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.UserManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestManager extends TestCase {
    private Manager manager;

    public Manager getManager() {
        return manager;
    }

    public void reset () {
        super.reset();

        manager = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupMiranda();
        manager = new Cluster(getMockNetwork(), true);
    }

    @Test
    public void testConstructor () {
        assert (getManager().getName().equals(Cluster.NAME));
        assert (getManager().getData() != null);
        assert (getManager().getData().size() < 1);
    }

    @Test
    public void testConvertList () {
        List<NodeElement> temp = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        temp.add(nodeElement);
        nodeElement = new NodeElement("bar.com", "192.168.1.2", 6789, "another node");
        temp.add(nodeElement);

        List<Node> nodes = getManager().convertList(temp);
    }

    @Test
    public void testConvert () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        Node node = (Node) getManager().convert(nodeElement);

        assert (node.getDns().equals("foo.com"));
        assert (node.getIp().equals("192.168.1.1"));
        assert (node.getPort() == 6789);
        assert (node.getDescription().equals("a node"));
    }

    @Test
    public void testPerformGarbageCollection () {
        getManager().setFile(getMockSingleFile());
        getManager().performGarbageCollection();

        verify(getMockSingleFile(), atLeastOnce()).sendGarbageCollectionMessage(Matchers.any(BlockingQueue.class),
                Matchers.any());
    }
}
