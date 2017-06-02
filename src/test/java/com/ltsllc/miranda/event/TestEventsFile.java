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

package com.ltsllc.miranda.event;

import com.ltsllc.miranda.file.states.SingleFileLoadingState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestEventsFile extends TestCase {
    private EventsFile eventsFile;

    public EventsFile getEventsFile() {
        return eventsFile;
    }

    public void reset () {
        super.reset();

        eventsFile = null;
    }

    public static final String TEST_FILE = "testFile";

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        eventsFile = new EventsFile(TEST_FILE, getMockReader(), getMockWriter());
    }

    @After
    public void cleanup () {
        deleteFile(TEST_FILE);
    }

    @Test
    public void testConstructor () {
        assert (getEventsFile().getFilename().equals("testFile"));
        assert (getEventsFile().getCurrentState() instanceof SingleFileLoadingState);
        assert (getEventsFile().getReader() == getMockReader());
        assert (getEventsFile().getWriter() == getMockWriter());
    }
}
