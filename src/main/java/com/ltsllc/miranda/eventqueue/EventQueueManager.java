package com.ltsllc.miranda.eventqueue;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.eventqueue.states.EventQueueManagerReadyState;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.manager.StandardManager;
import com.ltsllc.miranda.manager.states.DirectoryManagerStartState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A manager in charge of the event queue directory
 */
public class EventQueueManager extends DirectoryManager<EventQueue> {
    public EventQueueManager (String directory) throws IOException, MirandaException {
        super("event queue manager", directory, -1, Miranda.getInstance().getReader(),
                Miranda.getInstance().getWriter());

        setCurrentState(new DirectoryManagerStartState(this));
    }

    /**
     * Process a directory entry.
     * @param string The entry to be processed.  This is expected to be a full filename for the directory entry
     */
    @Override
    public void processEntry(String string) {
        if (string.endsWith("queue")) {
            try {
                FileReader fileReader = new FileReader(string);
                EventQueue eventQueue = getGson().fromJson(fileReader, EventQueue.class);
                eventQueue.rectify();
                eventQueue.start();
                getMap().put(string, eventQueue);
            } catch (Exception e) {
                Panic panic = new Panic ("Exception while processing " + string, e, Panic.Reasons.Exception);
                Miranda.panicMiranda(panic);
            }
        }
    }

    public State getReadyState()  {
        try {
            return new EventQueueManagerReadyState(this);
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception creating ready state", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return null;
        }
    }

    public EventQueue getEventQueueFor(String name) {
        return getMap().get(name);
    }

    public List<EventQueue> getEventQueues() {
        Collection collection = getMap().values();
        return new ArrayList<EventQueue>(collection);
    }
}

