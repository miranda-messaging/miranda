package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
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
public class TestFileWatcherReadyState extends TestCase {
    private static final String ROOT = "testdir";

    private static final String[][] FILE_SYSTEM_SPEC = {
            {"whatever", "random file"},
            {"new", "directory"},
            {"new/20170220-001.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
    };

    private FileWatcherService fileWatcherService;
    private BlockingQueue<Message> queue;

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public FileWatcherService getFileWatcherService() {
        return fileWatcherService;
    }

    public void reset() {
        super.reset();

        fileWatcherService = null;
        queue = null;
    }

    @Before
    public void setup() {
        reset();

        setuplog4j();
        setupMirandaProperties();

        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        fileWatcherService = new FileWatcherService(500);
        fileWatcherService.start();

        queue = new LinkedBlockingQueue<Message>();
    }

    @After
    public void cleanup() {
        deleteDirectory(ROOT);
    }

    private static final String FILE_NAME = "testdir/new/20170220-001.msg";

    @Test
    public void testProcessWatchMessage() {
        File file = new File(FILE_NAME);
        FileChangedMessage fileChangedMessage = new FileChangedMessage(null, this, file);
        WatchMessage message = new WatchMessage(getQueue(), this, file, fileChangedMessage);
        send(message, getFileWatcherService().getQueue());

        pause(125);

        touch(file);

        pause(1000);

        assert (contains(Message.Subjects.FileChanged, getQueue()));
    }


    @Test
    public void testProcessUnwatchFileMessage() {
        File file = new File(FILE_NAME);
        UnwatchFileMessage unwatchFileMessage = new UnwatchFileMessage(getQueue(), this, file);

        FileChangedMessage fileChangedMessage = new FileChangedMessage(getQueue(), this, file);
        WatchMessage watchMessage = new WatchMessage(getQueue(), this, file, fileChangedMessage);

        send(watchMessage, getFileWatcherService().getQueue());

        pause(125);

        touch(file);

        pause(1000);

        assert (contains(Message.Subjects.FileChanged, getQueue()));
        send(unwatchFileMessage, getFileWatcherService().getQueue());

        pause(125);
        getQueue().clear();

        touch(file);

        pause(1000);

        assert (!contains(Message.Subjects.FileChanged, getQueue()));
    }
}
