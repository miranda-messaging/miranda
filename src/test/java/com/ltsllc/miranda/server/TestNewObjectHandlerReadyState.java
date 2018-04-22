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

package com.ltsllc.miranda.server;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.NewUserHandler;
import com.ltsllc.miranda.user.states.NewUserHandlerReadyState;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestNewObjectHandlerReadyState extends TestCase {
    @Mock
    private Consumer mockConsumer;

    @Mock
    private NewUserHandler mockNewUserHandler;

    private NewObjectHandlerReadyState readyState;

    public NewUserHandler getMockNewUserHandler() {
        return mockNewUserHandler;
    }

    public Consumer getMockConsumer() {

        return mockConsumer;
    }

    public NewObjectHandlerReadyState getReadyState() {
        return readyState;
    }

    public void reset () throws Exception {
        super.reset();

        mockNewUserHandler = null;
        mockConsumer = null;
        readyState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        mockNewUserHandler = mock(NewUserHandler.class);
        mockConsumer = mock(Consumer.class);
        readyState = new NewUserHandlerReadyState(mockConsumer, getMockUsersFile(), mockNewUserHandler);
    }


}
