package com.ltsllc.miranda.operations.syncfiles.states;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.syncfiles.SyncFiles;

import java.security.GeneralSecurityException;

/**
 * A start state for SyncFiles
 */
public class SyncFilesStartState extends SyncFilesState {
    public SyncFilesStartState(SyncFiles syncFiles) {
        super (syncFiles);
    }


    public State start ()  {
        try {
            Miranda miranda = Miranda.getInstance();

            getCluster().sendGetVersion(getSyncFiles());
            getSyncFiles().addVersion(Files.Topic, miranda.getTopicManager().getVersion(), null);
            getSyncFiles().addVersion(Files.User, miranda.getUserManager().getVersion(), null);
            getSyncFiles().addVersion(Files.Subscription, miranda.getSubscriptionManager().getVersion(), null);
            getSyncFiles().addVersion(Files.Cluster, miranda.getCluster().getVersion(), null);
            getSyncFiles().addVersion(Files.DeliveriesList, miranda.getDeliveryManager().getVersion(), null);
            getSyncFiles().addVersion(Files.EventList, miranda.getEventManager().getVersion(), null);
        } catch (GeneralSecurityException e) {
            Panic panic = new Panic("Exception in SyncFilesStartState",   e);
            Miranda.panicMiranda(panic);
        }
        return new SyncFilesWaitingState(getSyncFiles());
    }
}
