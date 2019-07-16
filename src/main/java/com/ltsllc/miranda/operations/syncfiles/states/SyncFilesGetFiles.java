package com.ltsllc.miranda.operations.syncfiles.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.operations.syncfiles.SyncFiles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * The State that the SyncFiles goes into after issuing GetFils
 */
public class SyncFilesGetFiles extends SyncFilesState {
    public SyncFilesGetFiles(SyncFiles syncFiles) {
        super(syncFiles);
    }

    public State start () {
        Map<Files, Boolean>  waitingOn = new HashMap<>();

        Node node = getSyncFiles().getNodeFor(Files.DeliveriesList);
        if (node != null) {
            getFile(getSyncFiles().getQueue(), getSyncFiles(), Files.DeliveriesList, node);
            waitingOn.put(Files.DeliveriesList, Boolean.TRUE);
        }

        node = getSyncFiles().getNodeFor(Files.EventList);
        if (node != null) {
            getFile(getSyncFiles().getQueue(), getSyncFiles(), Files.EventList,node);
            waitingOn.put(Files.EventList, Boolean.TRUE) ;
        }

        node = getSyncFiles().getNodeFor(Files.Cluster);
        if (null != node) {
            getFile(getSyncFiles().getQueue(), getSyncFiles(), Files.Cluster, node);
            waitingOn.put(Files.Cluster, Boolean.TRUE);
        }

        node = getSyncFiles().getNodeFor(Files.Subscription);
        if (null != node) {
            getFile(getSyncFiles().getQueue(), getSyncFiles(), Files.Subscription, node);
            waitingOn.put(Files.Subscription, Boolean.TRUE);
        }

        node = getSyncFiles().getNodeFor(Files.Topic);
        if (null != node) {
            getFile(getSyncFiles().getQueue(), getSyncFiles(), Files.Topic, node);
            waitingOn.put(Files.Topic, Boolean.TRUE);
        }

        node = getSyncFiles().getNodeFor(Files.User);
        if (null != node) {
            getFile(getSyncFiles().getQueue(), getSyncFiles(), Files.User, node);
            waitingOn.put(Files.User, Boolean.TRUE);
        }

        if (waitingOn.size() != 0) {
            return new SyncFilesWaitingOnGet(getSyncFiles(),waitingOn);
        } else {
            try {
                return new SyncFilesStopState(getSyncFiles());
            } catch (MirandaException e) {
                Panic panic = new Panic("exception in SyncFilesGetFiles", e);
                Miranda.panicMiranda(panic);
                return StopState.getInstance();
            }
        }
    }

    public void getFile(BlockingQueue<Message> senderQueue, Object sender, Files file, Node node) {
        node.sendGetFile(senderQueue, sender, file);
    }
}
