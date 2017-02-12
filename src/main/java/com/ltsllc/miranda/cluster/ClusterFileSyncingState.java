package com.ltsllc.miranda.cluster;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.SingleFileSyncingState;
import com.ltsllc.miranda.node.NodeElement;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/12/2017.
 */
public class ClusterFileSyncingState extends SingleFileSyncingState {
    private ClusterFile clusterFile;

    public ClusterFileSyncingState (ClusterFile clusterFile) {
        super(clusterFile);

        this.clusterFile = clusterFile;
    }


    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    @Override
    public Type getListType() {
        return new TypeToken<List<NodeElement>>(){}.getType();
    }


    @Override
    public boolean contains(Object o) {
        NodeElement nodeElement = (NodeElement) o;

        for (NodeElement element : getClusterFile().getData()) {
            if (element.equals(nodeElement))
                return true;
        }

        return false;
    }


    @Override
    public State getReadyState() {
        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(getClusterFile());
        return clusterFileReadyState;
    }


    @Override
    public List getData() {
        return getClusterFile().getData();
    }


    @Override
    public String getName() {
        return "cluster";
    }


    @Override
    public SingleFile getFile() {
        return getClusterFile();
    }
}
