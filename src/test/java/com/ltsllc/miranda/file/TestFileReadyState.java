package com.ltsllc.miranda.file;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/25/2017.
 */
public class TestFileReadyState extends TestCase {
    private static final String ROOT = "testdir";

    private static final String[][] FILE_SYSTEM_SPEC = {
            {"whatever", "random file"},
            {"new", "directory"},
            {"new/20170220-001.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
    };

    private FileReadyState fileReadyState;

    public FileReadyState getFileReadyState() {
        return fileReadyState;
    }

    public void reset() {
        this.fileReadyState = null;
    }

    @Before
    public void setup () {
        reset();
        setuplog4j();
        setupMirandaProperties();
        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        EventsFile eventsFile = new EventsFile("testdir/new/20170220-001.msg", getWriter());
        eventsFile.start();

        State state = eventsFile.getCurrentState();
        fileReadyState = (FileReadyState) state;
    }

    @After
    public void cleanup () {
        deleteDirectory(ROOT);
    }

    @Test
    public void testProcessMessageGarbageCollection () {
        long then = System.currentTimeMillis();

        GarbageCollectionMessage message = new GarbageCollectionMessage(null, this);
        send(message, getFileReadyState().getFile().getQueue());

        pause(125);

        assert (collectedAfter(then, getFileReadyState().getFile()));
    }
}
