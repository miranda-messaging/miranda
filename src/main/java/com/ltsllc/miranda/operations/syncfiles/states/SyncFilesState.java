package com.ltsllc.miranda.operations.syncfiles.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.syncfiles.SyncFiles;
import com.ltsllc.miranda.operations.syncfiles.VersionNode;

import java.util.HashMap;
import java.util.Map;

/**
 * A state associated with synchronizing
 */
public class SyncFilesState extends State {
    public SyncFilesState(SyncFiles syncFiles) {
        super(syncFiles);
    }
    public SyncFiles getSyncFiles() {
        return (SyncFiles) getContainer();
    }

    public Cluster getCluster () {
        return Miranda.getInstance().getCluster();
    }

    private Map<Files, VersionNode> filesToVersionNode = new HashMap<>();

}
