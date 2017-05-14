package com.ltsllc.miranda.event;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.states.LoadingState;

/**
 * Created by Clark on 5/14/2017.
 */
public class EventsFileLoadingState extends LoadingState {
    public EventsFile getEventsFile () {
        return (EventsFile) getContainer();
    }

    public EventsFileLoadingState (EventsFile eventsFile) {
        super(eventsFile);
    }

    public State getReadyState () {
        return new EventsFileReadyState(getEventsFile());
    }
}
