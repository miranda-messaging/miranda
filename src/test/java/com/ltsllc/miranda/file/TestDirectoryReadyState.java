package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.event.SystemMessages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class TestDirectoryReadyState extends TestCase {
    private static final String ROOT = "testdir";

    private static final String[][] FILE_SYSTEM_SPEC = {
            {"whatever", "random file"},
            {"new", "directory"},
            {"new/20170220-001.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
    };


    private Directory directory;


    public Directory getDirectory() {
        return directory;
    }

    public void reset () {
        super.reset();
        directory = null;
    }

    @Before
    public void setup () {
        reset();
        setupMirandaProperties();
        createEventHiearchicy(ROOT, FILE_SYSTEM_SPEC);

        directory = new SystemMessages(ROOT, getWriter());
        directory.start();
    }

    @After
    public void tearDown () {
        deleteDirectory(ROOT);
    }


    /**
     * Garbage collection is not so much of an issue with things that subclass
     * the {@link Directory} class.  This is because the two types of objects
     * that live in directories, {@link com.ltsllc.miranda.event.Event} and
     * {@link com.ltsllc.miranda.deliveries.Delivery}, don't expire or get
     * collected.  Nevertheless, test that garbage collection gets done.
     */
    @Test
    public void testProcessGarbageCollectionMessage () {
        long then = System.currentTimeMillis();

        setuplog4j();

        getDirectory().load();

        GarbageCollectionMessage message = new GarbageCollectionMessage(null, this);
        send(message, getDirectory().getQueue());

        pause(125);

        assert (collectedAfter(then, getDirectory().getFiles()));
    }

    @Test
    public void testWrite () {
        getDirectory().load();
        getDirectory().write();

        pause(125);

        assert (contains(Message.Subjects.Write, getWriter()));
    }
}
