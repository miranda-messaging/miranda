package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
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

    private static final String FILENAME = "testdir/new/20170220-001.msg";

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

        Miranda.fileWatcher = new FileWatcherService(500);
        Miranda.fileWatcher.start();

        getEventsFile().watch();

        pause(125);

        touch(getEventsFile().getFilename());

        pause(1000);

        assert (getEventsFile().getLastLoaded() > then);
    }

    private void changeEvent (Event event) {
        switch (event.getMethod()) {
            case DELETE: {
                event.setMethod(Event.Methods.POST);
                break;
            }

            case GET: {
                event.setMethod(Event.Methods.POST);
                break;
            }

            case PUT: {
                event.setMethod(Event.Methods.POST);
                break;
            }

            case POST: {
                event.setMethod(Event.Methods.DELETE);
                break;
            }
        }
    }

    @Test
    public void testUpdateVersion () {
        getEventsFile().load();
        Version oldVersion = getEventsFile().getVersion();

        Event event = getEventsFile().getData().get(0);
        changeEvent (event);
        getEventsFile().updateVersion();

        Version newVersion = getEventsFile().getVersion();

        assert (!oldVersion.equals(newVersion));
    }

    @Test
    public void testEquals () {
        getEventsFile().load();
        EventsFile other = new EventsFile(FILENAME, getWriter());
        other.load();

        assert (other.equals(getEventsFile()));

        Event event = other.getData().get(0);
        changeEvent(event);
        other.updateVersion();

        assert (!other.equals(getEventsFile()));
    }
}