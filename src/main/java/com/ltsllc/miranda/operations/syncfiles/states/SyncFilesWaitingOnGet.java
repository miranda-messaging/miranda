package com.ltsllc.miranda.operations.syncfiles.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.syncfiles.SyncFiles;

import java.io.IOException;
import java.util.Map;

public class SyncFilesWaitingOnGet extends SyncFilesState {
    public SyncFilesWaitingOnGet (SyncFiles  syncFiles, Map<Files, Boolean> waitingOn) {
        super(syncFiles);
        getSyncFiles().setWaitingOn(waitingOn);
    }

    public State processMessage (Message message)
        throws MirandaException
    {
        State nextState = getSyncFiles().getCurrentState();

        try {
            switch (message.getSubject()) {
                case GetFileResponse:
                    GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                    nextState = processGetFileResponseMessage(getFileResponseMessage);
                    break;

                default:
                    nextState = super.processMessage(message);
                    break;
            }
        } catch (IOException e) {
            MirandaException mirandaException = new MirandaException(e);
            throw mirandaException;
        }

        return nextState;
    }

    public State processGetFileResponseMessage(GetFileResponseMessage getFileResponseMessage)
        throws IOException, MirandaException
    {

        Miranda miranda = Miranda.getInstance();

        switch (getFileResponseMessage.getFile()) {
            case DeliveriesList: {
                break;
            }

            case EventList: {
                break;
            }

            case Cluster: {
                miranda.getCluster().sendNewVersionMessag(getSyncFiles().getQueue(), getSyncFiles(), getFileResponseMessage.getContentAsBytes());
                getSyncFiles().getWaitingOn().remove(Files.Cluster);
                break;
            }

            case Topic: {
                miranda.getTopicManager().sendNewVersionMessag(getSyncFiles().getQueue(), getSyncFiles(), getFileResponseMessage.getContentAsBytes());
                getSyncFiles().getWaitingOn().remove(Files.Topic);
                break;
            }

            case User: {
                miranda.getUserManager().sendNewVersionMessag(getSyncFiles().getQueue(), getSyncFiles(), getFileResponseMessage.getContentAsBytes());
                getSyncFiles().getWaitingOn().remove(Files.User);
                break;
            }

            case Subscription: {
                miranda.getSubscriptionManager().sendNewVersionMessag(getSyncFiles().getQueue(), getSyncFiles(), getFileResponseMessage.getContentAsBytes());
                break;
            }

            default: {
                MirandaException mirandaException = new MirandaException("Unrecognized file:" +getFileResponseMessage.getFile());
                throw mirandaException;
            }
        }

        if (getSyncFiles().getWaitingOn().size() == 0) {
            return StopState.getInstance();
        } else {
            return getSyncFiles().getCurrentState();
        }
    }
}
