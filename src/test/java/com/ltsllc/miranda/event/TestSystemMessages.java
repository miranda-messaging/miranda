package com.ltsllc.miranda.event;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.property.MirandaProperties;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestSystemMessages extends TestCase {
    private SystemMessages systemMessages;

    public SystemMessages getSystemMessages() {
        return systemMessages;
    }

    @Before
    public void setup() {
        reset();
        setupMirandaProperties();

        MirandaProperties properties = Miranda.properties;
        String directory = properties.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);
        systemMessages = new SystemMessages(directory, getWriter());
    }

    @Test
    public void testConstructor() {
        MirandaProperties properties = Miranda.properties;
        String directory = properties.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);
        assert (getSystemMessages().getFilename().equals(directory));
        assert (getSystemMessages().getCurrentState() instanceof SystemMessagesReadyState);
    }

    @Test
    public void testIsFileOfInterest() {
        String interesting = "20170220-002.msg";
        String notInteresting = "junk";
        assert (getSystemMessages().isFileOfInterest(interesting));
        assert (!getSystemMessages().isFileOfInterest(notInteresting));
    }
}