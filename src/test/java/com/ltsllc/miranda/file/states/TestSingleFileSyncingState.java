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

package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.user.states.UsersFileSyncingState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/16/2017.
 */
public class TestSingleFileSyncingState extends TestCase {
    @Mock
    private UsersFile mockUsesFile;

    private UsersFileSyncingState usersFileSyncingState;

    public UsersFile getMockUsesFile() {
        return mockUsesFile;
    }

    public UsersFileSyncingState getUsersFileSyncingState() {
        return usersFileSyncingState;
    }

    public void reset () throws Exception {
        super.reset();

        this.mockUsesFile = null;
        this.usersFileSyncingState = null;
    }


    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        this.mockUsesFile = mock(UsersFile.class);
        this.usersFileSyncingState = new UsersFileSyncingState(mockUsesFile);
    }


    @Test
    public void testProcessGetFileResponse () throws MirandaException {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(null, this, "whatever");
        getUsersFileSyncingState().processMessage(getFileResponseMessage);

        verify(getMockUsesFile(), atLeastOnce()).merge(Matchers.anyList());
    }

    private static final byte[] TEST_FILE_CONTENTS = "whatever".getBytes();

    @Test
    public void testProcessGetFileMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetFileMessage getFileMessage = new GetFileMessage(queue, this, "whatever");

        when(getMockUsesFile().getBytes()).thenReturn(TEST_FILE_CONTENTS);
        getUsersFileSyncingState().processMessage(getFileMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
    }
}
