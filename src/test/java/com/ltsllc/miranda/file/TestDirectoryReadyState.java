package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.event.SystemMessages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    }

    @After
    public void tearDown () {
        deleteDirectory(ROOT);
    }

    @Test
    public void testProcessGarbageCollectionMessage () {
        GarbageCollectionMessage message = new GarbageCollectionMessage(null, this);
        send(message, getDirectory().getQueue());
    }

    @Test
    public void testWrite () {
        getDirectory().load();
        getDirectory().write();

        pause(125);

        assert (contains(Message.Subjects.Write, getWriter()));
    }
}
