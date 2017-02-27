package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 2/19/2017.
 */
public class DirectoryReadyState extends State {
    private static Logger logger = Logger.getLogger(FileReadyState.class);

    private Directory directory;

    public DirectoryReadyState(Directory directory) {
        super(directory);

        this.directory = directory;
    }

    public Directory getDirectory() {
        return directory;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            default :
                nextState = super.processMessage(message);
                break;
        }
        return nextState;
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        logger.info ("Garbage collecting " + getDirectory().getFilename());

        for (MirandaFile mirandaFile : getDirectory().getFiles())
        {
            mirandaFile.setLastCollection(System.currentTimeMillis());
        }

        getDirectory().setLastCollection(System.currentTimeMillis());

        return this;
    }
}
