package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 2/25/2017.
 */
public class FileWatcherReadyState extends State {
    private FileWatcherService fileWatcherService;

    public FileWatcherService getFileWatcherService() {
        return fileWatcherService;
    }

    public FileWatcherReadyState (FileWatcherService fileWatcherService) {
        super(fileWatcherService);

        this.fileWatcherService = fileWatcherService;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Watch: {
                WatchMessage watchMessage = (WatchMessage) message;
                nextState = processWatchMessage(watchMessage);
                break;
            }

            case UnwatchFile: {
                UnwatchFileMessage unwatchFileMessage = (UnwatchFileMessage) message;
                nextState = processUnwatchFileMessage (unwatchFileMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processWatchMessage (WatchMessage watchMessage) {
        getFileWatcherService().watch(watchMessage.getFile(), watchMessage.getSender(), watchMessage.getMessage());

        return this;
    }

    private State processUnwatchFileMessage (UnwatchFileMessage unwatchFileMessage) {
        getFileWatcherService().stopWatching(unwatchFileMessage.getFile(), unwatchFileMessage.getSender());

        return this;
    }
}
