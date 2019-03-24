package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.event.EventDirectory;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class TestMirandaDirectory extends TestCase {
    public EventDirectory eventDirectory;

    public EventDirectory getEventDirectory() {
        return eventDirectory;
    }

    public void setEventDirectory(EventDirectory eventDirectory) {
        this.eventDirectory = eventDirectory;
    }

    @Before
    public void setup () throws Exception {
        super.setup();
        EventDirectory temp = new EventDirectory("Whatever", 1000, getMockReader(),
                getMockWriter());

        setEventDirectory(temp);
        setupMockMiranda();
        setupMockReader();
        setupMockWriter();
    }

    @Test
    public void testLoad () {
        File temp = new File("C:/Users/miranda/IdeaProjects/miranda/data");
        getEventDirectory().setDirectoryName("data");
        getEventDirectory().setDirectory(temp);
        getEventDirectory().load();

        verify(getMockReader(), atLeastOnce()).sendReadMessage(any(), any(), anyString());

    }
}
