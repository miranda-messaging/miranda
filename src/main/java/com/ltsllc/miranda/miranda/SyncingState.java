package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.file.SubscriptionsFile;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UsersFile;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Clark on 2/10/2017.
 */

/**
 * Represents that Miranda is trying to synchronize with a remote node.
 */
public class SyncingState extends State {
    private Miranda miranda;
    private Set<String> filesOutstanding = new HashSet<String>();

    public SyncingState(Miranda miranda) {
        super(miranda);

        getFilesOutstanding().add("cluster");
        getFilesOutstanding().add("users");
        getFilesOutstanding().add("topics");
        getFilesOutstanding().add("subscriptions");

        this.miranda = miranda;
    }

    public Miranda getMiranda() {
        return miranda;
    }

    public Set<String> getFilesOutstanding() {
        return filesOutstanding;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case DoneSynchronizing : {
                DoneSynchronizingMessage doneSynchronizingMessage = (DoneSynchronizingMessage) message;
                nextState = processDoneSynchronizingMessage(doneSynchronizingMessage);
                break;
            }
            default :
                nextState = super.processMessage(message);
        }

        return nextState;
    }


    private State processDoneSynchronizingMessage (DoneSynchronizingMessage doneSynchronizingMessage) {
        State nextState = this;

        if (doneSynchronizingMessage.getSender() == ClusterFile.getInstance().getQueue()) {
            getFilesOutstanding().remove("cluster");
        } else if (doneSynchronizingMessage.getSender() == UsersFile.getInstance().getQueue()) {
            getFilesOutstanding().remove("users");
        } else if (doneSynchronizingMessage.getSender() == TopicsFile.getInstance().getQueue()) {
            getFilesOutstanding().remove("topics");
        } else if (doneSynchronizingMessage.getSender() == SubscriptionsFile.getInstance()) {
            getFilesOutstanding().remove("subscriptions");
        }

        if (getFilesOutstanding().size() <= 0) {
            nextState = new ReadyState(getMiranda());
        }

        return nextState;
    }
}
