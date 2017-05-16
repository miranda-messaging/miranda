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

package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.servlet.cluster.ClusterStatus;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;
import com.ltsllc.miranda.servlet.cluster.ClusterStatusObject;
import com.ltsllc.miranda.servlet.status.NodeStatus;
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
