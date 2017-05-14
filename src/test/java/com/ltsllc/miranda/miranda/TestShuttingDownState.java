package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.states.ShuttingDownState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by Clark on 5/4/2017.
 */
public class TestShuttingDownState extends TestCase {
    private ShuttingDownState shuttingDownState;

    public ShuttingDownState getShuttingDownState() {
        return shuttingDownState;
    }

    public void setShuttingDownState(ShuttingDownState shuttingDownState) {
        this.shuttingDownState = shuttingDownState;
    }

    public void reset () {
        super.reset();

        shuttingDownState = null;
    }

    @Before
    public void setup () {
        super.setup();

        shuttingDownState = new ShuttingDownState(getMockMiranda());
    }

    @Test
    public void testShutdownResponseIntermediate () {
        ShutdownResponseMessage shutdownMessage = new ShutdownResponseMessage(null, this, Cluster.NAME);

        when(getMockMiranda().getCurrentState()).thenReturn(getShuttingDownState());
        when(getMockMiranda().readyToShutDown()).thenReturn(false);

        State nextState = getShuttingDownState().processMessage(shutdownMessage);

        verify(getMockMiranda(), atLeastOnce()).getCurrentState();
        assert (nextState == getShuttingDownState());
    }

    public void setupMockLogger () {
        ShuttingDownState.setLogger(getMockLogger());
    }

    @Test
    public void testShuttingDownFinal () {
        setupMockLogger();
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(null, this, Cluster.NAME);

        when(getMockMiranda().readyToShutDown()).thenReturn(true);

        State nextState = getShuttingDownState().processMessage(shutdownResponseMessage);

        assert (nextState == StopState.getInstance());
        verify(getMockLogger(), atLeastOnce()).info(Matchers.any());
    }
}
