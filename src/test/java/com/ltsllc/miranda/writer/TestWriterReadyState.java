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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.doThrow;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestWriterReadyState extends TestCase {
    private WriterReadyState writerReadyState;

    public WriterReadyState getWriterReadyState() {
        return writerReadyState;
    }

    public void reset () {
        super.reset();

        writerReadyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        writerReadyState = new WriterReadyState(getMockWriter());
    }

    public static final String TEST_FILE_NAME = "testfile";
    public static final byte[] TEST_DATA = { 1, 2, 3, 4 };

    @Test
    public void testProcessWriteMessageSuccess () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        WriteMessage writeMessage = new WriteMessage(TEST_FILE_NAME, TEST_DATA, queue, this);

        State nextState = getWriterReadyState().processMessage(writeMessage);

        assert (contains(Message.Subjects.WriteSucceeded, queue));
        assert (nextState instanceof WriterReadyState);
    }

    @Test
    public void testProcessWriteMessageException () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        WriteMessage writeMessage = new WriteMessage(TEST_FILE_NAME, TEST_DATA, queue, this);

        IOException ioException = new IOException("test");
        try {
            doThrow(ioException).when(getMockWriter()).write(Matchers.anyString(), Matchers.any(byte[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        State nextState = getWriterReadyState().processMessage(writeMessage);

        assert (nextState instanceof WriterReadyState);
        assert (contains(Message.Subjects.WriteFailed, queue));
    }
}
