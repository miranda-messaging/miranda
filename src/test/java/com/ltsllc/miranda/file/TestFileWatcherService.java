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
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.file.messages.UnwatchFileMessage;
import com.ltsllc.miranda.file.messages.WatchMessage;
import com.ltsllc.miranda.file.states.FileWatcherReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    public FileWatcherService getFileWatcherService() {
        return fileWatcherService;
    }

    public void reset () {
        super.reset();

        if (null != Miranda.getInstance())
            Miranda.getInstance().reset();

        fileWatcherService = null;
        queue = null;
    }


    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupMirandaProperties();
        setupFileWatcher(100);

        createFileSystem(ROOT, FILE_SYSTEM_SPEC);

        this.fileWatcherService = Miranda.fileWatcher;
        this.queue = new LinkedBlockingQueue<Message>();
    }

    @After
    public void cleanup () {
        deleteDirectory(ROOT);
    }

    @Test
    public void testConstructor () {
        assert (getFileWatcherService().getCurrentState() instanceof FileWatcherReadyState);
    }

    /**
     * Note that this also tests the {@link FileWatcherService#watch(File, BlockingQueue)}
     * and {@link FileWatcherService#fireChanged(String)} methods.
     */
    @Test
    public void testCheckFiles () throws IOException{
        File file = new File(FILENAME);
        WatchMessage watchMessage = new WatchMessage(getQueue(), this, file);
        send(watchMessage, getFileWatcherService().getQueue());

        pause(125);

        String canonicalName = file.getCanonicalPath();
        List<FileWatcher> list = getFileWatcherService().getWatchers().get(canonicalName);
        Long value = getFileWatcherService().getWatchedFiles().get(canonicalName);
        assert (null != list);
        assert (null != value);
    }

    @Test
    public void testStopWatching () {
        File file = new File(FILENAME);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        WatchMessage watchMessage = new WatchMessage(getQueue(), this, file);
        send(watchMessage, getFileWatcherService().getQueue());

        pause(125);

        long time = touch(file);
        time++;
        touch (file, time);

        pause(200);

        getFileWatcherService().checkFiles();

        assert (contains(Message.Subjects.FileChanged, getQueue()));

        UnwatchFileMessage unwatchFileMessage = new UnwatchFileMessage(getQueue(), this, file);
        send(unwatchFileMessage, getFileWatcherService().getQueue());

        pause (250);

        getQueue().clear();

        time = touch(file);
        time++;
        touch(file, time);

        pause(250);

        assert (!(contains(Message.Subjects.FileChanged, getQueue())));
    }
}
