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

package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.Results;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.messages.CheckSessionResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestServletHolderReadyState extends TestCase {
    private ServletHolderReadyState readyState;

    public ServletHolderReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        readyState = new ServletHolderReadyState(getMockServletHolder());
    }

    @Test
    public void testCheckSessionResponseMethodSessionExists () {
        CheckSessionResponseMessage checkSessionResponseMessage = new CheckSessionResponseMessage(null, this,
                Results.Success, getMockSession());

        when(getMockServletHolder().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(checkSessionResponseMessage);

        assert (nextState == getReadyState());
        verify (getMockServletHolder(), atLeastOnce()).setSessionAndAwaken(Matchers.any(Session.class));
    }
}
