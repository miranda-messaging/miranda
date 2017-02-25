package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/19/2017.
 */
public class FileReadyState extends State {
    private static Logger logger = Logger.getLogger(FileReadyState.class);

    private MirandaFile file;


    public FileReadyState (MirandaFile file) {
        super(file);

        this.file = file;
    }


    public MirandaFile getFile() {
        return file;
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
        long now = System.currentTimeMillis();

        List<Perishable> expired = new ArrayList<Perishable>();

        for (Perishable perishable : getFile().getElements()) {
            if (perishable.expired(now)) {
                expired.add(perishable);
            }
        }

        getFile().getElements().removeAll(expired);

        getFile().setLastCollection(now);

        return this;
    }
}
