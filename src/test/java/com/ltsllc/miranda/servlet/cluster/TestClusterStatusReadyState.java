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

package com.ltsllc.miranda.servlet.cluster;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestClusterStatusReadyState extends TestCase {
    @Mock
    private ClusterStatus mockClusterStatus;

    private ClusterStatusReadyState readyState;

    public ClusterStatusReadyState getReadyState() {
        return readyState;
    }

    public ClusterStatus getMockClusterStatus() {
        return mockClusterStatus;
    }

    public void reset () throws MirandaException {
        super.reset();

        mockClusterStatus = null;
        readyState = null;
    }

    @Before
    public void setup () throws MirandaException {
        reset();

        super.setup();

        mockClusterStatus = mock(ClusterStatus.class);
        readyState = new ClusterStatusReadyState(mockClusterStatus);
    }

    @Test
    public void testProcessGetStatusResponseMessage () throws MirandaException {
        GetStatusResponseMessage getStatusResponseMessage = new GetStatusResponseMessage(null, this,
                null);

        State nextState = getReadyState().processMessage(getStatusResponseMessage);

        assert (nextState == getReadyState());
        verify (getMockClusterStatus(), atLeastOnce()).receivedClusterStatus(Matchers.eq(getStatusResponseMessage));
    }
}
