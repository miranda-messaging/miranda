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

package com.ltsllc.miranda.servlet.miranda;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.servlet.property.Property;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestMirandaStatusReadyState extends TestCase {
    @Mock
    private MirandaStatus mockMirandaStatus;

    private MirandaStatusReadyState readyState;

    public MirandaStatus getMockMirandaStatus() {
        return mockMirandaStatus;
    }

    public MirandaStatusReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        mockMirandaStatus = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockMirandaStatus = mock(MirandaStatus.class);
        readyState = new MirandaStatusReadyState(mockMirandaStatus);
    }

    @Test
    public void testProcessGetStatusResponseMessage () {
        com.ltsllc.miranda.servlet.status.StatusObject statusObject = new com.ltsllc.miranda.servlet.status.StatusObject(
                null,
                new ArrayList<Property>(),
                new ArrayList<NodeElement>()

        );

        GetStatusResponseMessage getStatusResponseMessage = new GetStatusResponseMessage(null, this,
                statusObject);

        State nextState = getReadyState().processMessage(getStatusResponseMessage);

        assert (nextState == getReadyState());
        verify(getMockMirandaStatus(), atLeastOnce()).receivedStatus(Matchers.any(com.ltsllc.miranda.servlet.status.StatusObject.class));
    }
}
