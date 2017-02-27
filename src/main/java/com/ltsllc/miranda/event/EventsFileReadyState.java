package com.ltsllc.miranda.event;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.file.FileReadyState;
import com.ltsllc.miranda.file.MirandaFileReadyState;

/**
 * Created by Clark on 2/19/2017.
 */
public class EventsFileReadyState extends MirandaFileReadyState {
    public EventsFileReadyState (EventsFile eventsFile) {
        super(eventsFile);
    }
}
