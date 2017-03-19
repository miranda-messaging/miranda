package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.LoadResponseMessage;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.cluster.messages.LoadMessage;

/**
 * Created by Clark on 3/12/2017.
 */
public class ClusterLoadingState extends State {
    private boolean deferredConnect;
    private boolean seenLoad;

    public boolean getDeferredConnect () {
        return deferredConnect;
    }

    public void setDeferredConnect (boolean deferredConnect) {
        this.deferredConnect = deferredConnect;
    }

    public boolean getSeenLoad () {
        return seenLoad;
    }

    public void setSeenLoad (boolean seenLoad) {
        this.seenLoad = seenLoad;
    }

    public Cluster getCluster () {
        return (Cluster) getContainer();
    }

    public ClusterLoadingState (Cluster cluster) {
        super (cluster);
        this.deferredConnect = false;
        this.seenLoad = false;
    }

    @Override
    public State start() {
        LoadMessage loadMessage = new LoadMessage(getCluster().getQueue(), this);
        send(getCluster().getClusterFileQueue(), loadMessage);

        return this;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case LoadResponse: {
                LoadResponseMessage loadResponseMessage = (LoadResponseMessage) message;
                nextState = processLoadResonseMessage(loadResponseMessage);
                break;
            }

            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) message;
                nextState = processConnectMessage(connectMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processLoadResonseMessage (LoadResponseMessage loadResponseMessage) {
        State nextState = this;

        setSeenLoad(true);

        getCluster().replaceNodes(loadResponseMessage.getData());

        if (getDeferredConnect()) {
            getCluster().connect();
            nextState = new ClusterReadyState(getCluster());
        }

        return nextState;
    }

    private State processConnectMessage (ConnectMessage connectMessage) {
        State nextState = this;

        if (getSeenLoad()) {
            getCluster().connect();
            nextState = new ClusterReadyState(getCluster());
        } else {
            setDeferredConnect(true);
        }

        return nextState;
    }


}
