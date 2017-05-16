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
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class TestFileWatcher extends TestCase {
    private static final String ROOT = "testdir";

    private static final String[][] FILE_SYSTEM_SPEC = {
            {"whatever", "random file"},
            {"new", "directory"},
            {"new/20170220-001.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
    };

    private FileWatcher fileWatcher;
    private BlockingQueue<Message> queue;

    public FileWatcher getFileWatcher() {
        return fileWatcher;
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public void reset () {
        super.reset();

        fileWatcher = null;
        queue = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setupMirandaProperties();

        createFileSystem(ROOT, FILE_SYSTEM_SPEC);

        queue = new LinkedBlockingQueue<Message>();
        File file = new File("testdir/new/20170220-001.msg");

        FileChangedMessage message = new FileChangedMessage(queue, this, file);
        fileWatcher = new FileWatcher(queue, message);
    }

    @After
    public void cleanup () {
        deleteDirectory(ROOT);
    }

    @Test
    public void testSend () {
        getFileWatcher().sendMessage();

        pause(125);

        assert (contains(Message.Subjects.FileChanged, getQueue()));
    }
}
