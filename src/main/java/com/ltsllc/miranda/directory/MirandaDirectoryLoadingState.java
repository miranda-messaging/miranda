package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.ReadResponseMessage;

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

    public MirandaDirectoryLoadingState (MirandaDirectory mirandaDirectory, int filesToLoad) {
        super(mirandaDirectory);

        this.filesToLoad = filesToLoad;
    }

    public MirandaDirectoryLoadingState (MirandaDirectory mirandaDirectory) {
        super(mirandaDirectory);
    }

    public void decrementFilesToLoad () {
        filesToLoad--;
    }

    public boolean loadedAllFiles () {
        return filesToLoad <= 0;
    }

    public MirandaDirectory getMirandaDirectory () {
        return (MirandaDirectory) getContainer();
    }

    public State processMessage (Message message) {
        State nextState = getMirandaDirectory().getCurrentState();
        switch (message.getSubject())  {
            case ReadResponse: {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage (readResponseMessage);
                break;
            }

            default: {
                defer(message);
                break;
            }
        }

        return nextState;
    }

    public State processReadResponseMessage (ReadResponseMessage readResponseMessage) {
        getMirandaDirectory().fileLoaded(readResponseMessage.getFilename(), readResponseMessage.getData());
        decrementFilesToLoad();
        if (loadedAllFiles()) {
            return new MirandaDirectoryReadyState(getMirandaDirectory());
        }

        return getMirandaDirectory().getCurrentState();
    }
}
