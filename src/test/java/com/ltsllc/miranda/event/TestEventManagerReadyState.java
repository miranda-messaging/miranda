package com.ltsllc.miranda.event;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestEventManagerReadyState extends TestCase {
    @Mock
    private EventManager mockEventManager;

    private EventManagerReadyState readyState;

    public EventManager getMockEventManager() {
        return mockEventManager;
    }

    public EventManagerReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        mockEventManager = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockEventManager = mock(EventManager.class);
        readyState = new EventManagerReadyState(mockEventManager);
    }
}
