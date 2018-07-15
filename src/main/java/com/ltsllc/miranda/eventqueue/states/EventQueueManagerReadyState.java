package com.ltsllc.miranda.eventqueue.states;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.eventqueue.EventQueueManager;
import com.ltsllc.miranda.manager.states.DirectoryManagerReadyState;

/**
 * The {@link com.ltsllc.miranda.eventqueue.EventQueueManager} is ready to process messages.
 */
public class EventQueueManagerReadyState extends DirectoryManagerReadyState {
    public EventQueueManagerReadyState (EventQueueManager eventQueueManager) throws MirandaException {
        super(eventQueueManager);
    }
}
