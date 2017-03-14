package com.ltsllc.miranda.event;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.writer.Writer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */
public class EventsFile extends SingleFile<Event> {
    public EventsFile (String filename, Writer writer) {
        super(filename, writer);

        setCurrentState(new EventsFileReadyState(this));
    }

    public Type listType() {
        return new TypeToken<List<Event>>(){}.getType();
    }

    public List buildEmptyList() {
        return new ArrayList<Event>();
    }
}
