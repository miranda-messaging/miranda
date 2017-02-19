package com.ltsllc.miranda.event;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.file.FileReadyState;

/**
 * Created by Clark on 2/19/2017.
 */
public class EventsFileReadyState extends FileReadyState {
    public EventsFileReadyState (EventsFile eventsFile) {
        super(eventsFile);
    }
}
