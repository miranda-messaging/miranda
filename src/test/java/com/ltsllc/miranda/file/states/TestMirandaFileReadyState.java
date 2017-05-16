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

import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.file.states.MirandaFileReadyState;
import com.ltsllc.miranda.test.TestCase;
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

        createFileSystem(ROOT, FILE_SYSTEM_SPEC);
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
