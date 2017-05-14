package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.manager.ManagerLoadingState;
import com.ltsllc.miranda.test.TestCase;

/**
 * Created by Clark on 5/14/2017.
 */
public class ClusterLoadingState extends ManagerLoadingState {
    public Cluster getCluster () {
        return (Cluster) getContainer();
    }

    public ClusterLoadingState (Cluster cluster) {
        super(cluster);
    }

    public State getReadyState () {
        return new ClusterReadyState(getCluster());
    }
}
