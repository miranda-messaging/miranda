/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.states.ShuttingDownState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;


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

    public void reset () throws MirandaException {
        super.reset();

        shuttingDownState = null;
    }

    @Before
    public void setup () throws MirandaException {
        super.setup();

        shuttingDownState = new ShuttingDownState(getMockMiranda());
    }

    @Test
    public void testShutdownResponseIntermediate () throws MirandaException {
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
    public void testShuttingDownFinal () throws MirandaException {
        setupMockLogger();
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(null, this, Cluster.NAME);

        when(getMockMiranda().readyToShutDown()).thenReturn(true);

        State nextState = getShuttingDownState().processMessage(shutdownResponseMessage);

        assert (nextState == StopState.getInstance());
        verify(getMockLogger(), atLeastOnce()).info(Matchers.any());
    }
}
