package com.ltsllc.miranda.file;

import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.writer.Writer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by Clark on 2/26/2017.
 */
public class TestMirandaFileReadyState extends TestCase {
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
    private MirandaFileReadyState mirandaFileReadyState;

    public EventsFile getEventsFile() {
        return eventsFile;
    }

    public MirandaFileReadyState getMirandaFileReadyState() {
        return mirandaFileReadyState;
    }

    public void reset () {
        super.reset();
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupWriter();
        setupMirandaProperties();

        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);
        setupFileWatcher(100);

        this.eventsFile = new EventsFile(FILENAME, Writer.getInstance());
        this.eventsFile.start();

        this.mirandaFileReadyState = (MirandaFileReadyState) eventsFile.getCurrentState();
    }

    @Test
    public void testProcessFileChangedMessage () {
        getEventsFile().load();
        long then = System.currentTimeMillis();

        File file = new File(FILENAME);
        FileChangedMessage message = new FileChangedMessage(null,this, file);
        send(message, getEventsFile().getQueue());

        pause(125);

        touch(file);

        pause(250);

        assert (then < System.currentTimeMillis());
        assert (getEventsFile().getLastLoaded() > then);
    }
}
