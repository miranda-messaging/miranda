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

import com.ltsllc.miranda.event.states.EventManagerReadyState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/13/2017.
 */
public class TestEventManager extends TestCase {
    private EventManager eventManager;

    public EventManager getEventManager () {
        return eventManager;
    }

    public void reset () {
        eventManager = null;
    }

    @Before
    public void setup () {
        try {
            reset();

            super.setup();

            setuplog4j();
            setupMockFileWatcherService();
            createDirectory("testDirectory");
            eventManager = new EventManager("testDirectory", 1000000, getMockReader(), getMockWriter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor () {
        assert (getEventManager().getDirectory() != null);
        assert (getEventManager().getDirectory().getDirectory().getName().equals("testDirectory"));
        assert (getEventManager().getCurrentState() instanceof EventManagerReadyState);
    }
}
