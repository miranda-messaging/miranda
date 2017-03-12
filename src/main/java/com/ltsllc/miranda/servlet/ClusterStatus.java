package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;

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
        notifyAll();
    }

    public ClusterStatusObject getClusterStatus () {
        setClusterStatusObject(null);

        try {
            synchronized (this) {
                wait(1000);
            }
        } catch (InterruptedException e) {
            Panic panic = new Panic("Exception waiting for cluster status", e, Panic.Reasons.ServletTimeout);
            Miranda.getInstance().panic(panic);
        }

        GetStatusMessage getStatusMessage = new GetStatusMessage(getQueue(), this);
        send(getStatusMessage, Miranda.getInstance().getCluster());

        return getClusterStatusObject();
    }
}
