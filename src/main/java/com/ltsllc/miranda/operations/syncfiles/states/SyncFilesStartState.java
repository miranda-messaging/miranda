package com.ltsllc.miranda.operations.syncfiles.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.mina.SslException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.syncfiles.BootstrapOperation;
import com.ltsllc.miranda.operations.syncfiles.SyncFiles;
import com.ltsllc.miranda.servlet.bootstrap.BootstrapMessage;
import com.ltsllc.miranda.topics.TopicManager;

import java.security.GeneralSecurityException;

public class SyncFilesStartState extends SyncFilesState {
    private BootstrapOperation operation;

    public BootstrapOperation getOperation() {
        return operation;
    }

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
