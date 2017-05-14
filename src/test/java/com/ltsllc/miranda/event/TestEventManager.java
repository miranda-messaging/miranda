package com.ltsllc.miranda.event;

import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.writer.Writer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 5/13/2017.
 */
public class TestEventManager extends TestCase {
    private EventManager eventManager;

    public EventManager getEventManager () {
        return eventManager;
    }

    public void reset () {
        eventManager = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupMockFileWatcherService();
        createDirectory("testDirectory");
        eventManager = new EventManager("testDirectory", getMockReader(), getMockWriter());
    }

    @Test
    public void testConstructor () {
        assert (getEventManager().getDirectory() != null);
        assert (getEventManager().getDirectory().getDirectory().getName().equals("testDirectory"));
        assert (getEventManager().getCurrentState() instanceof EventManagerReadyState);
    }
}
