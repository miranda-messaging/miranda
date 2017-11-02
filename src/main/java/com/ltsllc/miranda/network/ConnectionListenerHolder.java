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

package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.cluster.Cluster;

/**
 * Created by Clark on 5/23/2017.
 */
public class ConnectionListenerHolder extends Consumer {
    public static String NAME = "network listener";

    private ConnectionListener networkListener;
    private Cluster cluster;
    private Network network;

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public ConnectionListener getNetworkListener() {
        return networkListener;
    }

    public void setNetworkListener(ConnectionListener networkListener) {
        this.networkListener = networkListener;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public ConnectionListenerHolder(ConnectionListener networkListener, Cluster cluster) {
        super(NAME);

        this.networkListener = networkListener;
        this.cluster = cluster;

        ConnectionListenerHolderReadyState readyState = new ConnectionListenerHolderReadyState(this);
        setCurrentState(readyState);
    }

    public void stop () {
       getNetworkListener().stop();
    }
}
