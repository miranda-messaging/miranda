package com.ltsllc.miranda.file;

import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.writer.Writer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @Mock
    private EventsFile mockEventsFile;

    private MirandaFileReadyState mirandaFileReadyState;

    public EventsFile getMockEventsFile() {
        return mockEventsFile;
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

        this.mockEventsFile = mock(EventsFile.class);
        this.mirandaFileReadyState = new MirandaFileReadyState(getMockEventsFile());
    }

    @After
    public void cleanup () {
        deleteDirectory(ROOT);
    }

    @Test
    public void testProcessFileChangedMessage () {
        File file = new File(FILENAME);
        FileChangedMessage message = new FileChangedMessage(null,this, file);
        getMirandaFileReadyState().processMessage(message);

        verify(getMockEventsFile(), atLeastOnce()).load();
    }
}
