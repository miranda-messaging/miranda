package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 2/25/2017.
 */
public class TestMirandaFile extends TestCase {
    private static final String ROOT = "testdir";

    private static final String[][] FILE_SYSTEM_SPEC = {
            {"whatever", "random file"},
            {"new", "directory"},
            {"new/20170220-001.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
    };

    private EventsFile eventsFile;

    public EventsFile getEventsFile() {
        return eventsFile;
    }

    @Override
    public void reset() {
        super.reset();

        eventsFile = null;
    }

    @Before
    public void setup() {
        reset();
        setuplog4j();
        setupMirandaProperties();

        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        eventsFile = new EventsFile("testdir/new/20170220-001.msg", getWriter());
        eventsFile.start();
        eventsFile.load();
    }

    @After
    public void cleanup() {
        deleteDirectory(ROOT);
    }

    @Test
    public void testWrite() {
        getEventsFile().write();

        assert (contains(Message.Subjects.Write, getWriter()));
    }

    @Test
    public void testVersion() {
        Version oldVersion = getEventsFile().getVersion();

        deleteDirectory(ROOT);
        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        getEventsFile().fileChanged();

        Version newVersion = getEventsFile().getVersion();

        assert (!oldVersion.equals(newVersion));
    }

    @Test
    public void testFileChanged() {
        Event oldEvent = getEventsFile().getData().get(0);

        deleteDirectory(ROOT);
        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        getEventsFile().fileChanged();
        Event newEvent = getEventsFile().getData().get(0);

        assert (oldEvent != newEvent);
    }


    @Test
    public void testWatch() {
        long then = System.currentTimeMillis();

        MirandaProperties properties = MirandaProperties.getInstance();
        properties.setProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD, "1000");
        Miranda.initialize();

        getEventsFile().watch();

        pause(125);

        touch(getEventsFile().getFilename());

        pause(2000);

        assert (getEventsFile().getLastLoaded() > then);
    }
}
