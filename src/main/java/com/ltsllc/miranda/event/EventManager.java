package com.ltsllc.miranda.event;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Manager;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

/**
 * Created by Clark on 5/1/2017.
 */
public class EventManager extends DirectoryManager {
    public static final String NAME = "event manager";

    public EventManager (String directoryName, Reader reader, Writer writer) {
        super(NAME, directoryName, reader, writer);
    }
}
