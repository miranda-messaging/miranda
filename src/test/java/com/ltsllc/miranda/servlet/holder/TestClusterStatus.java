package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.servlet.cluster.ClusterStatus;
import com.ltsllc.miranda.servlet.messages.GetStatusResponseMessage;
import com.ltsllc.miranda.servlet.cluster.ClusterStatusObject;
import com.ltsllc.miranda.servlet.objects.NodeStatus;
import com.ltsllc.miranda.servlet.cluster.ClusterStatusReadyState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 3/23/2017.
 */
public class TestClusterStatus extends TestCase {
    private ClusterStatus clusterStatus;

    public ClusterStatus getClusterStatus() {
        return clusterStatus;
    }

    public void reset () {
        super.reset();

        clusterStatus = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        clusterStatus = new ClusterStatus();
    }

    @Test
    public void testConstructor () {
        assert (getClusterStatus().getCurrentState() instanceof ClusterStatusReadyState);
    }

    @Test
    public void testReceivedClusterStatus () {
        List<NodeStatus> nodeStatusList = new ArrayList<NodeStatus>();
        ClusterStatusObject clusterStatusObject = new ClusterStatusObject(nodeStatusList);
        GetStatusResponseMessage getStatusResponseMessage = new GetStatusResponseMessage(null, this, clusterStatusObject);

        assert (null == getClusterStatus().getClusterStatusObject());

        getClusterStatus().receivedClusterStatus(getStatusResponseMessage);

        assert (clusterStatusObject == getClusterStatus().getClusterStatusObject());
    }
}
