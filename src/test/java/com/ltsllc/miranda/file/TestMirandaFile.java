package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.main.Ready;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

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

    public static long touch(String filename, long time) {
        File file = new File(filename);

        if (!file.setLastModified(time)) {
            Exception e = new Exception("could not set the time of last modification of " + file);
            e.printStackTrace();
            System.exit(1);
        }

        return file.lastModified();
    }

    public static long touch(String filename) {
        long now = System.currentTimeMillis();
        return touch(filename, now);
    }


    @Test
    public void testWatch() {
        Miranda.initialize();
        long then = System.currentTimeMillis();
        getEventsFile().watch();

        pause(125);

        touch(getEventsFile().getFilename());

        pause(2000);

        assert (getEventsFile().getLastLoaded() > then);
    }
}
