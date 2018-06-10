package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.FileChangedMessage;

/**
 * Created by Clark on 6/8/2017.
 */
public class DirectoryManagerReadyState extends State {
    public DirectoryManagerReadyState(DirectoryManager directoryManager) throws MirandaException {
        super(directoryManager);
    }

    public DirectoryManager getDirectoryManager() {
        return (DirectoryManager) getContainer();
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getDirectoryManager().getCurrentState();

        switch (message.getSubject()) {
            case FileChanged: {
                FileChangedMessage fileChangedMessage = (FileChangedMessage) message;
                nextState = processFileChangedMessage(fileChangedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }

    public State processFileChangedMessage(FileChangedMessage fileChangedMessage) {
        return getDirectoryManager().getCurrentState();
    }
}
