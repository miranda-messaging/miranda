package com.ltsllc.miranda.cluster;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.network.NewConnectionMessage;
import com.ltsllc.miranda.node.*;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */

/**
 *
 */
public class ClusterSyncingState extends State {
    private static Logger logger = Logger.getLogger(ClusterSyncingState.class);
    private static Gson ourGson = new Gson();

    private Cluster cluster;

    public Cluster getCluster() {
        return cluster;
    }

    public ClusterSyncingState(Consumer consumer, Cluster cluster) {
        super(consumer);
        this.cluster = cluster;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case ClusterFile: {
                ClusterFileMessage clusterFileMessage = (ClusterFileMessage) message;
                nextState = processClusterFileMessage(clusterFileMessage);
                break;
            }

            case ClusterFileChanged: {
                ClusterFileChangedMessage clusterFileChangedMessage = (ClusterFileChangedMessage) message;
                nextState = processClusterFileChangedMessage(clusterFileChangedMessage);
                break;
            }


            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    /**
     * Called when we have determined that a remote cluster file is more recent than our
     * local cluster file.
     *
     * @param clusterFileMessage
     * @return
     */
    private State processClusterFileMessage(ClusterFileMessage clusterFileMessage) {
        NewClusterFileMessage newClusterFileMessage = new NewClusterFileMessage(getCluster().getQueue(), this,
                clusterFileMessage.getFile(), clusterFileMessage.getVersion());
        send(getCluster().getClusterFileQueue(), newClusterFileMessage);

        return this;
    }


    private State processClusterFileChangedMessage (ClusterFileChangedMessage clusterFileChangedMessage) {
        for (NodeElement nodeElement : clusterFileChangedMessage.getFile()) {
            if (!getCluster().contains(nodeElement) && !equalsYourself(nodeElement)) {
                Node node = new Node(nodeElement, getCluster().getNetwork());
                node.start();
                node.connect();
                getCluster().getNodes().add(node);
            }
        }

        ClusterReadyState clusterReadyState = new ClusterReadyState(getCluster());
        return clusterReadyState;
    }

    private boolean equalsYourself (NodeElement nodeElement) {
        String myDns = System.getProperty(MirandaProperties.PROPERTY_MY_DNS);
        int myPort = MirandaProperties.getInstance().getIntegerProperty(MirandaProperties.PROPERTY_MY_PORT);

        return myDns.equals(nodeElement.getDns()) && myPort == nodeElement.getPort();
    }

}
