package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
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

        Miranda.reset();
        fileWatcherService = null;
        queue = null;
    }

    @Before
    public void setup() {
        reset();

        setuplog4j();
        setupMirandaProperties();

        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        MirandaProperties properties = MirandaProperties.getInstance();
        properties.setProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD, "1000");
        int period = properties.getIntegerProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD);

        Miranda.initialize();

        fileWatcherService = Miranda.fileWatcher;
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

        Miranda.initialize();

        WatchMessage message = new WatchMessage(getQueue(), this, file, fileChangedMessage);
        send(message, Miranda.fileWatcher.getQueue());

        pause(125);

        touch(file);

        pause(2000);

        assert (contains(Message.Subjects.FileChanged, getQueue()));
    }


    @Test
    public void testProcessUnwatchFileMessage() {
        File file = new File(FILE_NAME);
        UnwatchFileMessage unwatchFileMessage = new UnwatchFileMessage(getQueue(), this, file);

        MirandaProperties properties = MirandaProperties.getInstance();
        properties.setProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD, "500");

        FileChangedMessage fileChangedMessage = new FileChangedMessage(getQueue(), this, file);
        WatchMessage watchMessage = new WatchMessage(getQueue(), this, file, fileChangedMessage);

        Miranda.initialize();

        send(watchMessage, Miranda.fileWatcher.getQueue());

        pause(125);

        touch(file);

        pause(1000);

        assert (contains(Message.Subjects.FileChanged, getQueue()));
        send(unwatchFileMessage, Miranda.fileWatcher.getQueue());

        pause(125);
        getQueue().clear();

        touch(file);

        pause(1000);

        assert (!contains(Message.Subjects.FileChanged, getQueue()));
    }
}
