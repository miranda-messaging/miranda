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

package com.ltsllc.miranda.servlet.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.objects.ClusterStatusObject;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;

/**
 * Created by Clark on 3/10/2017.
 */
public class ClusterStatus extends Consumer {
    private static ClusterStatus ourInstance;

    private ClusterStatusObject clusterStatusObject;

    public static ClusterStatus getInstance() {
        return ourInstance;
    }

    public static synchronized void initialize() throws MirandaException {
        if (null == ourInstance) {
            ourInstance = new ClusterStatus();
        }
    }


    public ClusterStatusObject getClusterStatusObject() {
        return clusterStatusObject;
    }

    public void setClusterStatusObject(ClusterStatusObject clusterStatusObject) {
        this.clusterStatusObject = clusterStatusObject;
    }

    public ClusterStatus() throws MirandaException {
        super("cluster status");

        ClusterStatusReadyState clusterStatusReadyState = new ClusterStatusReadyState(this);
        setCurrentState(clusterStatusReadyState);
    }

    public void receivedClusterStatus(GetStatusResponseMessage message) {
        ClusterStatusObject clusterStatusObject = (ClusterStatusObject) message.getStatusObject();
        setClusterStatusObject(clusterStatusObject);
        synchronized (this) {
            notifyAll();
        }
    }

    public ClusterStatusObject getClusterStatus() {
        setClusterStatusObject(null);

        Miranda.getInstance().getCluster().sendGetStatus(getQueue(), this);
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
