package com.ltsllc.miranda.event;

import com.ltsllc.miranda.directory.MirandaDirectory;


/**
 * Created by Clark on 5/13/2017.
 */
public class EventDirectory extends MirandaDirectory {
    public EventDirectory (String directoryName) {
        super(directoryName);
    }

    public static final String EVENT_FILE = ".event";

    public boolean isInteresting (String name) {
        return name.endsWith(EVENT_FILE);
    }
}
