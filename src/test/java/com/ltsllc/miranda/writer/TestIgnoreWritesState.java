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

package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestIgnoreWritesState extends TestCase {
    private IgnoreWritesState  ignoreWritesState;

    public IgnoreWritesState getIgnoreWritesState() {
        return ignoreWritesState;
    }

    public void reset () throws Exception {
        super.reset();

        ignoreWritesState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        ignoreWritesState = new IgnoreWritesState(getMockWriter());
    }

    public static byte[] TEST_DATA = {1, 2, 3, 4};

    @Test
    public void testProcessWriteMessage () throws MirandaException {
        setuplog4j();
        IgnoreWritesState.setLogger(getMockLogger());
        WriteMessage writeMessage = new WriteMessage("whatever", TEST_DATA, null, this);

        State nextState = getIgnoreWritesState().processMessage(writeMessage);

        assert (nextState instanceof IgnoreWritesState);
        verify (getMockLogger(), atLeastOnce()).warn(Matchers.anyString());
    }
}
