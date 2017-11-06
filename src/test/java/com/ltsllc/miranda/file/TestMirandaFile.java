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

package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

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
    public void reset() throws MirandaException {
        super.reset();

        eventsFile = null;
    }

    @Before
    public void setup() {
        try {
            reset();

            super.setup();

            setuplog4j();
            setupMiranda();
            setupMockFileWatcher();
            setupMockReader();
            setupMirandaProperties();
            setupWriter();

            Event event = new Event(Event.Methods.POST, "010203");
            List<Event> eventList = new ArrayList<Event>();
            eventList.add(event);

            eventsFile = new EventsFile("whatever", getMockReader(), getMockWriter());
            eventsFile.setData(eventList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanup() {
        deleteDirectory(ROOT);
    }

    @Test
    public void testWrite() {
        getEventsFile().write();

        verify(getMockWriter(), atLeastOnce()).sendWrite(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyString(), Matchers.any(byte[].class));
    }

    @Test
    public void testVersion() throws Exception {
        Version oldVersion = getEventsFile().getVersion();

        assert (oldVersion.equals(oldVersion));

        Event event = new Event(Event.Methods.POST, "010203");
        List<Event> eventList = new ArrayList<Event>();
        eventList.add(event);

        getEventsFile().setData(eventList);

        getEventsFile().recalculateVersion();

        Version newVersion = getEventsFile().getVersion();

        assert (!oldVersion.equals(newVersion));
    }


    @Test
    public void testFileChanged() throws IOException, MirandaException {
        setupMiranda();
        setupMockReader();
        File file = new File(getEventsFile().getFilename());
        getEventsFile().fileChanged();
        verify(getMockReader(), atLeastOnce()).sendReadMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.eq(getEventsFile().getFilename()));
    }


    @Test
    public void testWatch() {
        getEventsFile().watch();
        File file = new File(getEventsFile().getFilename());
        verify(getMockFileWatcherService(), atLeastOnce()).sendWatchFileMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(file), Matchers.any(BlockingQueue.class));
    }

    private void changeEvent(Event event) {
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
    public void testUpdateVersion() throws MirandaException {
        setupMiranda();
        setupMockReader();
        getEventsFile().load();
        Version oldVersion = getEventsFile().getVersion();

        Event event = getEventsFile().getData().get(0);
        changeEvent(event);
        getEventsFile().updateVersion();

        Version newVersion = getEventsFile().getVersion();

        assert (!oldVersion.equals(newVersion));
    }

    @Test
    public void testEquals() throws IOException, MirandaException {
        assert (getEventsFile().equals(getEventsFile()));

        EventsFile newEventsFile = new EventsFile("whatever", getMockReader(), getMockWriter());
        Event event = Event.createRandom();
        List<Event> eventList = new ArrayList<Event>();
        newEventsFile.setData(eventList);

        assert (!getEventsFile().equals(newEventsFile));
    }
}
