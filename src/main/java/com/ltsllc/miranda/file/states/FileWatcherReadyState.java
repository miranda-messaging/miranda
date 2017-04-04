package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.file.messages.UnwatchFileMessage;
import com.ltsllc.miranda.file.messages.WatchMessage;
import com.ltsllc.miranda.miranda.Miranda;

import java.io.IOException;

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
        try {
            getFileWatcherService().watch(watchMessage.getFile(), watchMessage.getSender(), watchMessage.getMessage());
        } catch (IOException e) {
            Panic panic = new Panic ("Exception watching file", e, Panic.Reasons.ExceptionInProcessMessage);
            Miranda.getInstance().panic(panic);
        }

        return this;
    }

    private State processUnwatchFileMessage (UnwatchFileMessage unwatchFileMessage) {
        try {
            getFileWatcherService().stopWatching(unwatchFileMessage.getFile(), unwatchFileMessage.getSender());
        } catch (IOException e) {
            Panic panic = new Panic("Exception trying to unwatch file", e, Panic.Reasons.ExceptionInProcessMessage);
            Miranda.getInstance().panic(panic);
        }

        return this;
    }
}
