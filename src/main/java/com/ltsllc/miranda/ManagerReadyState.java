package com.ltsllc.miranda;

import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;

import java.util.List;

/**
 * Created by Clark on 4/26/2017.
 */
public class ManagerReadyState<E,F> extends State {
    public Manager getManager () {
        return (Manager) getContainer();
    }

    public ManagerReadyState (Manager manager) {
        super(manager);
    }

    public State processMessage (Message message) {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage (shutdownMessage);
                break;
            }

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage(fileLoadedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processShutdownMessage (ShutdownMessage shutdownMessage) {
        ManagerShuttingDownState managerShuttingDownState = new ManagerShuttingDownState(getManager(),
                shutdownMessage.getSender());

        return managerShuttingDownState;
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) {
        List<F> data = (List<F>) fileLoadedMessage.getData();
        List<E> newList = getManager().convertList(data);
        getManager().setData(newList);

        return getManager().getCurrentState();
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getManager().performGarbageCollection();

        return getManager().getCurrentState();
    }
}
