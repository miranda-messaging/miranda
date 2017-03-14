package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.writer.Writer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 2/25/2017.
 */
public class TestDirectoryReadyState extends TestCase {
    private static final String ROOT = "testdir";

    private static final String[][] FILE_SYSTEM_SPEC = {
            {"whatever", "random file"},
            {"new", "directory"},
            {"new/20170220-001.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
    };

    @Mock
    private Directory mockDirectory;

    @Mock
    private MirandaFile mockMirandaFile;

    private DirectoryReadyState directoryReadyState;

    public DirectoryReadyState getDirectoryReadyState() {
        return directoryReadyState;
    }

    public Directory getMockDirectory() {
        return mockDirectory;
    }

    public MirandaFile getMockMirandaFile() {
        return mockMirandaFile;
    }

    public void reset () {
        super.reset();

        this.mockMirandaFile = null;
        this.directoryReadyState = null;
        this.mockDirectory = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setupWriter();
        setupMirandaProperties();

        this.mockMirandaFile = mock(MirandaFile.class);
        this.mockDirectory = mock(Directory.class);
        this.directoryReadyState = new DirectoryReadyState(getMockDirectory());
    }

    @After
    public void tearDown () {
        deleteDirectory(ROOT);
    }


    /**
     * Garbage collection is not so much of an issue with things that subclass
     * the {@link Directory} class.  This is because the two types of objects
     * that live in directories, {@link com.ltsllc.miranda.event.Event} and
     * {@link com.ltsllc.miranda.deliveries.Delivery}, don't expire or get
     * collected.  Nevertheless, test that garbage collection gets done.
     */
    @Test
    public void testProcessGarbageCollectionMessage () {
        setuplog4j();

        List<MirandaFile> files = new ArrayList<MirandaFile>();
        files.add(getMockMirandaFile());

        when(getMockDirectory().getFiles()).thenReturn(files);
        GarbageCollectionMessage message = new GarbageCollectionMessage(null, this);
        getDirectoryReadyState().processMessage(message);

        verify(getMockDirectory(), atLeastOnce()).getFiles();
        verify(getMockMirandaFile(), atLeastOnce()).performGarbageCollection();
    }

}
