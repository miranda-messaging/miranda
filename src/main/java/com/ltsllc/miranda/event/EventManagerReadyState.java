package com.ltsllc.miranda.event;

import com.ltsllc.miranda.State;

/**
 * Created by Clark on 5/14/2017.
 */
public class EventManagerReadyState extends State {
    public EventManager getEventManager () {
        return (EventManager) getContainer();
    }

    public EventManagerReadyState (EventManager eventManager) {
        super(eventManager);
    }
}
