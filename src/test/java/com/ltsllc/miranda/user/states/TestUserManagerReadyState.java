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

package com.ltsllc.miranda.user.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.messages.GetUserMessage;
import com.ltsllc.miranda.user.messages.ListUsersMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 4/2/2017.
 */
public class TestUserManagerReadyState extends TestCase {
    @Mock
    private UserManager mockUserManager;

    private UserManagerReadyState readyState;

    public UserManagerReadyState getReadyState() {
        return readyState;
    }

    public UserManager getMockUserManager() {
        return mockUserManager;
    }

    public void reset() throws MirandaException {
        super.reset();

        mockUserManager = null;
        readyState = null;
    }

    @Before
    public void setup() throws MirandaException {
        reset();

        super.setup();

        mockUserManager = mock(UserManager.class);
        readyState = new UserManagerReadyState(mockUserManager);
    }

    @Test
    public void testProcessGarbageCollection() throws MirandaException {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(null, this);

        when(getMockUserManager().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(garbageCollectionMessage);

        assert (nextState == getReadyState());
        verify(getMockUserManager(), atLeastOnce()).performGarbageCollection();
    }

    @Test
    public void testGetUser() throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetUserMessage getUserMessage = new GetUserMessage(queue, this, "whatever");

        when(getMockUserManager().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(getUserMessage);

        assert (nextState == getReadyState());
        verify(getMockUserManager(), atLeastOnce()).getUser(Matchers.eq("whatever"));
        assert (contains(Message.Subjects.GetUserResponse, queue));
    }

    @Test
    public void testProcessGetUsersMessage() throws MirandaException {
        User user = new User("whatever", "whatever");
        List<User> users = new ArrayList<User>();
        users.add(user);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        ListUsersMessage getUsersMessage = new ListUsersMessage(queue, this);

        when(getMockUserManager().getCurrentState()).thenReturn(getReadyState());
        when(getMockUserManager().getUsers()).thenReturn(users);

        State nextState = getReadyState().processMessage(getUsersMessage);

        assert (nextState == getReadyState());
        assert (contains(Message.Subjects.GetUsersResponse, queue));
    }
}
