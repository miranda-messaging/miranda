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

package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.user.states.UsersFileReadyState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestUsersFileReadyState extends TestCase {
    private UsersFileReadyState readyState;

    public UsersFileReadyState getReadyState() {
        return readyState;
    }

    public void reset () throws MirandaException {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup () throws MirandaException {
        reset();

        super.setup();

        setuplog4j();

        readyState = new UsersFileReadyState(getMockUsersFile());
    }

    @Test
    public void testProcessGetVersionMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetVersionMessage getVersionMessage = new GetVersionMessage(null, this, queue);

        State nextState = getReadyState().processMessage(getVersionMessage);

        assert (nextState instanceof UsersFileReadyState);
        assert (contains(Message.Subjects.Version, queue));
    }

    @Test
    public void testProcessNewUserMessage () throws MirandaException {
        User user = new User("whtever", "whatever");
        NewUserMessage newUserMessage = new NewUserMessage(null, this, user);

        State nextState = getReadyState().processMessage(newUserMessage);

        assert (nextState instanceof UsersFileReadyState);
        verify (getMockUsersFile(), atLeastOnce()).addUser(Matchers.eq(user));
    }

    @Test
    public void testProcessGetFileMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetFileMessage getFileMessage = new GetFileMessage(queue, this, "whatever");

        when(getMockUsersFile().getBytes()).thenReturn("whatever".getBytes());

        State nextState = getReadyState().processMessage(getFileMessage);

        assert (nextState instanceof UsersFileReadyState);
        assert (contains(Message.Subjects.GetFileResponse, queue));
    }
}
