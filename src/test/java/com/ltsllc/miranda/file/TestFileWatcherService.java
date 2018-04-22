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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.StopWatchingMessage;
import com.ltsllc.miranda.file.messages.WatchFileMessage;
import com.ltsllc.miranda.file.states.FileWatcherReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Clark on 2/26/2017.
 */
public class TestFileWatcherService extends TestCase {
    private static final String ROOT = "testdir";

    private static final String[][] FILE_SYSTEM_SPEC = {
            {"whatever", "random file"},
            {"new", "directory"},
            {"new/20170220-001.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
    };

    private static final String FILENAME = "testdir/new/20170220-001.msg";

    private FileWatcherService fileWatcherService;
    private BlockingQueue<Message> queue;

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public void reset () throws Exception {
        super.reset();

        if (null != Miranda.getInstance())
            Miranda.getInstance().reset();

        fileWatcherService = null;
        queue = null;
    }


    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();
        setupMirandaProperties();
        setupFileWatcherService(100);

        queue = new LinkedBlockingQueue<Message>();
        createFileSystem(ROOT, FILE_SYSTEM_SPEC);
    }

    @After
    public void cleanup () {
        deleteDirectory(ROOT);
    }

    @Test
    public void testConstructor () {
        assert (Miranda.fileWatcher.getCurrentState() instanceof FileWatcherReadyState);
    }

    @Test
    public void testStopWatching() throws IOException {
        File file = new File(FILENAME);
        Miranda.fileWatcher.watchFile(file, getQueue());

        pause(1000);

        touch(file);

        pause(1500);

        Miranda.fileWatcher.checkFiles();

        assert (contains(Message.Subjects.FileChanged, getQueue()));

        StopWatchingMessage stopWatchingMessage = new StopWatchingMessage(getQueue(), this, file, getQueue());
        send(stopWatchingMessage, Miranda.fileWatcher.getQueue());

        pause (250);

        getQueue().clear();

        touch(file);

        pause(1500);

        assert (!(contains(Message.Subjects.FileChanged, getQueue())));
    }
}
