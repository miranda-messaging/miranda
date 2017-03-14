package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ClusterStatusObject;
import com.ltsllc.miranda.servlet.states.ClusterStatusReadyState;
import com.ltsllc.miranda.servlet.GetStatusResponseMessage;

/**
 * Created by Clark on 3/10/2017.
 */
public class ClusterStatus extends Consumer {
    private static ClusterStatus ourInstance;

    private ClusterStatusObject clusterStatusObject;

    public static ClusterStatus getInstance () {
        return ourInstance;
    }

    public static synchronized void initialize () {
        if (null == ourInstance) {
            ourInstance = new ClusterStatus();
        }
    }


    public ClusterStatusObject getClusterStatusObject() {
        return clusterStatusObject;
    }

    public void setClusterStatusObject (ClusterStatusObject clusterStatusObject) {
        this.clusterStatusObject = clusterStatusObject;
    }

    private ClusterStatus () {
        super("cluster status");

        ClusterStatusReadyState clusterStatusReadyState = new ClusterStatusReadyState(this);
        setCurrentState(clusterStatusReadyState);
    }

    public void receivedClusterStatus (GetStatusResponseMessage message) {
        ClusterStatusObject clusterStatusObject = (ClusterStatusObject) message.getStatusObject();
        setClusterStatusObject(clusterStatusObject);
        synchronized (this) {
            notifyAll();
        }
    }

    public ClusterStatusObject getClusterStatus () {
        setClusterStatusObject(null);

        Cluster.getInstance().sendGetStatus(getQueue(), this);
        try {
            synchronized (this) {
                wait(1000);
            }
        } catch (InterruptedException e) {
            Panic panic = new Panic("Exception waiting for cluster status", e, Panic.Reasons.ServletTimeout);
            Miranda.getInstance().panic(panic);
        }

        return getClusterStatusObject();
    }
}
