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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
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

    public static final String TEST_DIRECTORY = "testdir";
    public static final String TEST_FILENAME = "testdir/whatever";





    @Before
    public void setup () {
        try {
            reset();

            super.setup();

            setupMirandaProperties();
            createFileSystem(ROOT, FILE_SYSTEM_SPEC);

            createDirectory(TEST_DIRECTORY);
            createFile(TEST_FILENAME);
            setupFileWatcherService(100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void cleanup () {

        deleteDirectory(ROOT);
    }

    @Test
    public void testCheck () throws Exception {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        File file = new File(TEST_FILENAME);
        Miranda.fileWatcher.watchFile(file, queue);

        assert (queueIsEmpty(queue));

        pause(500);

        touch(file);

        Miranda.fileWatcher.check();

        pause(1000);

        int x = 13;
        long filetime = file.lastModified();
        if (!contains(Message.Subjects.FileChanged, queue))
            x++;

        assert (contains(Message.Subjects.FileChanged, queue));
    }
}
