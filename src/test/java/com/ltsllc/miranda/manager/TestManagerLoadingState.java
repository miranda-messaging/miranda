package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.states.ClusterLoadingState;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.network.messages.NodeAddedMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestManagerLoadingState extends TestCase {
    private ManagerLoadingState loadingState;

    public ManagerLoadingState getLoadingState() {
        return loadingState;
    }

    public void reset () {
        super.reset();

        loadingState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        loadingState = new ClusterLoadingState(getMockCluster());
    }

    @Test
    public void testProcessFileLoadedMessage () {
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("fooo.com", "192.168.1.1", 6789, "a node");
        nodeElementList.add(nodeElement);

        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this, nodeElementList);

        State nextState = getLoadingState().processMessage(fileLoadedMessage);

        assert (nextState instanceof ManagerReadyState);
    }
}
