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

    @Override
    public void processEntry(String string) {
        EventQueue eventQueue = new EventQueue(string);
        eventQueue.start();
        getMap().put(string, eventQueue);
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
