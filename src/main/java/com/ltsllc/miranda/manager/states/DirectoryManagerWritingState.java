package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;

public class DirectoryManagerWritingState extends State {
    public DirectoryManagerWritingState (DirectoryManager directoryManager) {
        super(directoryManager);
    }

    public DirectoryManager getDirectoryManager () {
        return (DirectoryManager) getContainer();
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getDirectoryManager().getCurrentState();

        switch (message.getSubject())
        {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage (garbageCollectionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        return getDirectoryManager().getCurrentState();
    }

}
