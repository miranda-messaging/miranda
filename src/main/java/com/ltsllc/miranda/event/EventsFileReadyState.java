package com.ltsllc.miranda.event;

import com.ltsllc.miranda.file.states.MirandaFileReadyState;

/**
 * Created by Clark on 2/19/2017.
 */
public class EventsFileReadyState extends MirandaFileReadyState {
    public EventsFileReadyState (EventsFile eventsFile) {
        super(eventsFile);
    }
}
