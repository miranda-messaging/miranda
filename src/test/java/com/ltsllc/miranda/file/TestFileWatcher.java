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
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
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

    public static final String TEST_DIRECTORY = "testdir";
    public static final String TEST_FILENAME = "testdir/whatever";

    private FileWatcher fileWatcher;
    private BlockingQueue<Message> watcher;
    private File file;

    public File getFile() {
        return file;
    }

    public FileWatcher getFileWatcher() {
        return fileWatcher;
    }

    public BlockingQueue<Message> getWatcher() {
        return watcher;
    }

    public void reset () {
        super.reset();

        fileWatcher = null;
        watcher = null;
    }

    @Before
    public void setup () {
        try {
            reset();

            super.setup();

            setupMirandaProperties();

            createFileSystem(ROOT, FILE_SYSTEM_SPEC);

            file = new File(TEST_FILENAME);

            watcher = new LinkedBlockingQueue<Message>();

            createDirectory(TEST_DIRECTORY);
            createFile(TEST_FILENAME);

            File file = new File(TEST_FILENAME);
            fileWatcher = new SimpleFileWatcher(file, watcher);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void cleanup () {
        deleteDirectory(ROOT);
    }

    @Test
    public void testCheck () throws Exception {
        getFileWatcher().check();

        assert (queueIsEmpty(getWatcher()));

        touch(getFile());

        getFileWatcher().check();

        assert (contains(Message.Subjects.FileChanged, getWatcher()));
    }
}
