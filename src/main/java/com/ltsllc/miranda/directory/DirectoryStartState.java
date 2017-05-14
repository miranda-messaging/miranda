package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.miranda.Miranda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 5/13/2017.
 */
public class DirectoryStartState extends State {
    public MirandaDirectory getDirectory () {
        return (MirandaDirectory) getContainer();
    }

    public DirectoryStartState (MirandaDirectory directory) {
        super (directory);
    }

    public State processMessage (Message message) {
        State nextState = getDirectory().getCurrentState();

        switch (message.getSubject()) {
            case ScanCompleteMessage: {
                ScanCompleteMessage scanCompleteMessage = (ScanCompleteMessage) message;
                nextState = processScanCompleteMessage(scanCompleteMessage);
                break;
            }

            case ExceptionDuringScanMessage: {
                ExceptionDuringScanMessage exceptionDuringScanMessage = (ExceptionDuringScanMessage) message;
                nextState = processExceptionDuringScanMessage(exceptionDuringScanMessage);
                break;
            }

            default: {
                super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processScanCompleteMessage (ScanCompleteMessage scanCompleteMessage) {
        List<File> files = new ArrayList<File>(scanCompleteMessage.getFiles());
        getDirectory().setFiles(files);

        return getDirectory().getCurrentState();
    }

    public State processExceptionDuringScanMessage (ExceptionDuringScanMessage exceptionDuringScanMessage) {
        StartupPanic startupPanic = new StartupPanic("Exception scanning diresctory", StartupPanic.StartupReasons.ExceptionScanning);
        Miranda.getInstance().panic(startupPanic);

        return getDirectory().getCurrentState();
    }
}
