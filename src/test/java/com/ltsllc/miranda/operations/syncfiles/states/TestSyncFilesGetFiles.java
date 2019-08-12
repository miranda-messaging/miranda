package com.ltsllc.miranda.operations.syncfiles.states;


import com.ltsllc.miranda.operations.syncfiles.SyncFiles;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestSyncFilesGetFiles extends TestCase {
    private SyncFiles syncFiles;
    private SyncFilesGetFiles syncFilesGetFiles;

    public SyncFilesGetFiles getSyncFilesGetFiles() {
        return syncFilesGetFiles;
    }

    public void setSyncFilesGetFiles(SyncFilesGetFiles syncFilesGetFiles) {
        this.syncFilesGetFiles = syncFilesGetFiles;
    }

    public SyncFiles getSyncFiles() {
        return syncFiles;
    }

    public void setSyncFiles(SyncFiles syncFiles) {
        this.syncFiles = syncFiles;
    }

    public void reset () throws Exception {
        syncFiles = null;
        syncFilesGetFiles = null;
        super.reset();
    }

    @Before
    public void setup() throws Exception{
        reset();

        super.setup();

        setSyncFiles(mock(SyncFiles.class));
        setSyncFilesGetFiles(new SyncFilesGetFiles((getSyncFiles())));
    }

    @Mock
    private SyncFiles mockSyncFiles;

    @Test
    public void testConstructor()
    {
        assert(syncFilesGetFiles.getSyncFiles() == getSyncFiles());
    }

    @Test
    public void testStart() {
        when(getSyncFiles()).thenReturn(mockSyncFiles);

        getSyncFilesGetFiles().start();
    }
}
