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

package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.servlet.user.UserHolder;
import com.ltsllc.miranda.servlet.user.UserHolderReadyState;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.messages.GetUsersResponseMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestUsersHolderReadyState extends TestCase {
    @Mock
    private UserHolder mockUserHolder;

    private UserHolderReadyState usersHolderReadyState;

    public UserHolder getMockUserHolder() {
        return mockUserHolder;
    }

    public UserHolderReadyState getUserHolderReadyState() {
        return usersHolderReadyState;
    }

    public void reset() throws MirandaException {
        super.reset();

        mockUserHolder = null;
        usersHolderReadyState = null;
    }

    @Before
    public void setup() throws MirandaException {
        reset();

        super.setup();

        mockUserHolder = mock(UserHolder.class);
        usersHolderReadyState = new UserHolderReadyState(mockUserHolder);
    }

    @Test
    public void testProcessGetUsersResponseMessage() throws MirandaException {
        User user = new User("whatever", "whatever");
        List<User> users = new ArrayList<User>();
        users.add(user);
        GetUsersResponseMessage getUsersResponseMessage = new GetUsersResponseMessage(null, this, users);

        when(getMockUserHolder().getCurrentState()).thenReturn(getUserHolderReadyState());

        State nextState = getUserHolderReadyState().processMessage(getUsersResponseMessage);

        assert (nextState == getUserHolderReadyState());
        verify(getMockUserHolder(), atLeastOnce()).setUsersAndAwaken(Matchers.eq(users));
    }
}
