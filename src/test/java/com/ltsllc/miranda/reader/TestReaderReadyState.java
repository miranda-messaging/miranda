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

package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.when;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestReaderReadyState extends TestCase {
    private ReaderReadyState readerReadyState;

    public ReaderReadyState getReaderReadyState() {
        return readerReadyState;
    }

    public void reset () throws MirandaException {
        super.reset();

        readerReadyState = null;
    }

    @Before
    public void setup () throws MirandaException {
        reset();

        super.setup();

        readerReadyState = new ReaderReadyState(getMockReader());
    }

    @Test
    public void testProcessReadMessage () throws GeneralSecurityException, IOException, MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        ReadMessage readMessage = new ReadMessage(queue, this, "whatever");
        Reader.ReadResult result = new Reader.ReadResult();
        result.result = ReadResponseMessage.Results.Success;
        result.data = "whatever".getBytes();
        result.filename = "whatever";

        when(getMockReader().read(Matchers.anyString())).thenReturn(result);
        when(getMockReader().getCurrentState()).thenReturn(getReaderReadyState());

        State nextState = getReaderReadyState().processMessage(readMessage);

        assert (nextState == getReaderReadyState());
        assert (contains(Message.Subjects.ReadResponse, queue));
    }
}
