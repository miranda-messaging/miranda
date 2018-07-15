package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.reader.messages.ReadResponseMessage;

/**
 * Created by Clark on 6/7/2017.
 */
public class MirandaDirectoryLoadingState extends State {
    private int filesToLoad;

    public int getFilesToLoad() {
        return filesToLoad;
    }

    public void setFilesToLoad(int filesToLoad) {
        this.filesToLoad = filesToLoad;
    }

    public MirandaDirectoryLoadingState(MirandaDirectory mirandaDirectory, int filesToLoad) throws MirandaException {
        super(mirandaDirectory);

        this.filesToLoad = filesToLoad;
    }

    public MirandaDirectoryLoadingState(MirandaDirectory mirandaDirectory) throws MirandaException {
        super(mirandaDirectory);
    }

    public void decrementFilesToLoad() {
        filesToLoad--;
    }

    public boolean loadedAllFiles() {
        return filesToLoad <= 0;
    }

    public MirandaDirectory getMirandaDirectory() {
        return (MirandaDirectory) getContainer();
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getMirandaDirectory().getCurrentState();
        switch (message.getSubject()) {
            case ReadResponse: {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage(readResponseMessage);
                break;
            }

            case FileChanged: {
                FileChangedMessage fileChangedMessage = (FileChangedMessage) message;
                nextState = processFileChangedMessage(fileChangedMessage);
                break;
            }

            default: {
                defer(message);
                break;
            }
        }

        return nextState;
    }

    public State processReadResponseMessage(ReadResponseMessage readResponseMessage) throws MirandaException {
        getMirandaDirectory().fileLoaded(readResponseMessage.getFilename(), readResponseMessage.getData());
        decrementFilesToLoad();
        if (loadedAllFiles()) {
            return new MirandaDirectoryReadyState(getMirandaDirectory());
        }

        return getMirandaDirectory().getCurrentState();
    }

    public State processFileChangedMessage(FileChangedMessage fileChangedMessage) {
        getMirandaDirectory().load();

        return getMirandaDirectory().getCurrentState();
    }
}
