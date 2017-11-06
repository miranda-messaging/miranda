/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

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

    public void reset () throws MirandaException {
        super.reset();

        this.mockMirandaFile = null;
        this.directoryReadyState = null;
        this.mockDirectory = null;
    }

    @Before
    public void setup () throws MirandaException {
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
     * that live in directories, {@link com.ltsllc.miranda.clientinterface.basicclasses.Event} and
     * {@link com.ltsllc.miranda.clientinterface.basicclasses.Delivery}, don't expire or get
     * collected.  Nevertheless, test that garbage collection gets done.
     */
    @Test
    public void testProcessGarbageCollectionMessage () throws MirandaException {
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
