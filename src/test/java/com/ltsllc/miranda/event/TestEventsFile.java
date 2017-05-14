package com.ltsllc.miranda.event;

import com.ltsllc.miranda.file.states.LoadingState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestEventsFile extends TestCase {
    private EventsFile eventsFile;

    public EventsFile getEventsFile() {
        return eventsFile;
    }

    public void reset () {
        super.reset();

        eventsFile = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        eventsFile = new EventsFile("testFile", getMockReader(), getMockWriter());
    }

    @Test
    public void testConstructor () {
        assert (getEventsFile().getFilename().equals("testFile"));
        assert (getEventsFile().getCurrentState() instanceof LoadingState);
        assert (getEventsFile().getReader() == getMockReader());
        assert (getEventsFile().getWriter() == getMockWriter());
    }
}
