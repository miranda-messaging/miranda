package com.ltsllc.miranda.operations.syncfiles.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.operations.syncfiles.SyncFiles;
import com.ltsllc.miranda.operations.syncfiles.messages.GetVersionResponseMessage;

/**
 * The state the @link SyncFiles enters while it waiting for a reply
 * to the @link GetVersion message.
 */
public class SyncFilesWaitingState extends SyncFilesState {
    public SyncFilesWaitingState (SyncFiles syncFiles) {
        super(syncFiles);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getSyncFiles().getCurrentState();

        switch (message.getSubject()) {
            case GetVersionsResponse: {
                GetVersionResponseMessage getVersionResponseMessage = (GetVersionResponseMessage) message;
                nextState = processGetVersionResponseMessage(getVersionResponseMessage);
                break;
            }



            default :
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    public State processGetVersionResponseMessage(GetVersionResponseMessage getVersionResponseWireMessage) {
        getSyncFiles().addFiles(getVersionResponseWireMessage);
        return getSyncFiles().getCurrentState();
    }


}
