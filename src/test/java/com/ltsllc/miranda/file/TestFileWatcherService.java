package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
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

        Miranda.reset();
        fileWatcherService = null;
        queue = null;
    }


    @Before
    public void setup () {
        reset();

        setuplog4j();
        setupMirandaProperties();

        MirandaProperties properties = MirandaProperties.getInstance();
        properties.setProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD, "500");

        Miranda.initialize();

        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        fileWatcherService = Miranda.fileWatcher;
        queue = new LinkedBlockingQueue<Message>();
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
     * Note that this also tests the {@link FileWatcherService#watch(File, BlockingQueue, Message)}
     * and {@link FileWatcherService#fireChanged(String)} methods.
     */
    @Test
    public void testCheckFiles () {
        File file = new File(FILENAME);
        FileChangedMessage fileChangedMessage = new FileChangedMessage(null, this, file);
        WatchMessage watchMessage = new WatchMessage(getQueue(), this, file, fileChangedMessage);
        send(watchMessage, Miranda.fileWatcher.getQueue());

        pause(125);

        touch(file);

        pause(1000);

        assert (contains(Message.Subjects.FileChanged, getQueue()));
    }

    @Test
    public void testStopWatching () {
        File file = new File(FILENAME);
        FileChangedMessage fileChangedMessage = new FileChangedMessage(null, this, file);
        WatchMessage watchMessage = new WatchMessage(getQueue(), this, file, fileChangedMessage);
        send(watchMessage, Miranda.fileWatcher.getQueue());

        pause(125);

        touch(file);

        pause(1000);

        assert (contains(Message.Subjects.FileChanged, getQueue()));

        UnwatchFileMessage unwatchFileMessage = new UnwatchFileMessage(getQueue(), this, file);
        send(unwatchFileMessage, Miranda.fileWatcher.getQueue());

        pause (125);

        getQueue().clear();
        touch(file);

        pause(1000);

        assert (!(contains(Message.Subjects.FileChanged, getQueue())));
    }
}
