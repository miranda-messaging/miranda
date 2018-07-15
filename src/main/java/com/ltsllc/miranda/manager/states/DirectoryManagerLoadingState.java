package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.states.ShuttingDownState;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.reader.messages.ScanResponseMessage;

public class DirectoryManagerLoadingState extends State {
    private State readyState;

    public State getReadyState() {
        return readyState;
    }

    public void setReadyState(State readyState) {
        this.readyState = readyState;
    }

    public DirectoryManager getDirectoryManager () {
        return (DirectoryManager) getContainer();
    }

    public DirectoryManagerLoadingState (DirectoryManager directoryManager, State readyState) {
        setContainer(directoryManager);
        setReadyState(readyState);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getDirectoryManager().getCurrentState();

        switch (message.getSubject()) {
            case ScanResponse: {
                ScanResponseMessage scanResponseMessage = (ScanResponseMessage) message;
                nextState = processScanResponseMessage(scanResponseMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case Timeout: {
                defer(message);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }

    public State processScanResponseMessage (ScanResponseMessage scanResponseMessage) {
        State nextState = getDirectoryManager().getCurrentState();

        if (scanResponseMessage.getResult() == Results.Success) {
            for (String string : scanResponseMessage.getContents()) {
                getDirectoryManager().processEntry(string);
            }

            nextState = getDirectoryManager().getReadyState();
        }

        return nextState;
    }

    public State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        defer(garbageCollectionMessage);

        return getDirectoryManager().getCurrentState();
    }

}
