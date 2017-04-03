package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.file.messages.FileChangedMessage;

/**
 * Created by Clark on 2/26/2017.
 */
public class MirandaFileReadyState extends State {
    public MirandaFileReadyState (MirandaFile file) {
        super(file);
    }

    public MirandaFile getMirandaFile () {
        return (MirandaFile) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

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

    public void fireFileLoaded () {}

    public State processFileChangedMessage (FileChangedMessage fileChangedMessage) {
        getMirandaFile().load();

        fireFileLoaded();

        return getMirandaFile().getCurrentState();
    }
}
