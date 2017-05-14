package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;

import java.util.List;

/**
 * Created by Clark on 5/14/2017.
 */
abstract public class ManagerStartState extends State {
    abstract public State getReadyState();

    public Manager getManager () {
        return (Manager) getContainer();
    }

    public ManagerStartState (Manager manager) {
        super(manager);
    }

    public State processMessage (Message message) {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
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

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) {
        List list = (List) fileLoadedMessage.getData();
        getManager().setData(list);

        return getReadyState();
    }
}
