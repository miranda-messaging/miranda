package com.ltsllc.miranda.network;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.StopMessage;

/**
 * Created by Clark on 3/10/2017.
 */
public class NetworkListenerReadyState extends State {
    public NetworkListenerReadyState (NetworkListener networkListener) {
        super(networkListener);
    }

    public NetworkListener getNetworkListener () {
        return (NetworkListener) getContainer();
    }

    public State start () {
        getNetworkListener().getConnections();

        return this;
    }

    public State processStopMessage (StopMessage stopMessage) {
        getNetworkListener().stopListening();
        return getNetworkListener().getCurrentState();
    }
}
