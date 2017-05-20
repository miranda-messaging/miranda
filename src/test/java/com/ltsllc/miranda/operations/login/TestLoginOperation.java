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

package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestLoginOperation extends TestCase {
    private LoginOperation loginOperation;

    public LoginOperation getLoginOperation() {
        return loginOperation;
    }

    public void reset () {
        super.reset();

        loginOperation = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        loginOperation = new LoginOperation("whatever", new LinkedBlockingQueue<Message>());
    }

    @Test
    public void testConstructor () {
        assert (getLoginOperation().getUser().equals("whatever"));
        assert (getLoginOperation().getCurrentState() instanceof LoginOperationReadyState);
    }

    @Test
    public void testStart () {
        setupMockMiranda();
        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());

        getLoginOperation().start();

        verify(getMockSessionManager(), atLeastOnce()).sendGetSessionMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.anyString());
    }
}
