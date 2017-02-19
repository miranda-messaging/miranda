package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;

/**
 * Created by Clark on 2/19/2017.
 */
public class DirectoryReadyState extends State {
    private Directory directory;

    public DirectoryReadyState(Consumer consumer) {
        super(consumer);
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
        for (MirandaFile file : getDirectory().getFiles()) {
            GarbageCollectionMessage message = new GarbageCollectionMessage(getDirectory().getQueue(), this);
            send(file.getQueue(), message);
        }

        return this;
    }
}
