package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

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

    public void reset () {
        super.reset();

        readerReadyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        readerReadyState = new ReaderReadyState(getMockReader());
    }

    @Test
    public void testProcessReadMessage () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        ReadMessage readMessage = new ReadMessage(queue, this, "whatever");
        Reader.ReadResult result = new Reader.ReadResult();
        result.result = Results.Success;
        result.data = "whatever".getBytes();
        result.filename = "whatever";

        when(getMockReader().read(Matchers.anyString())).thenReturn(result);
        when(getMockReader().getCurrentState()).thenReturn(getReaderReadyState());

        State nextState = getReaderReadyState().processMessage(readMessage);

        assert (nextState == getReaderReadyState());
        assert (contains(Message.Subjects.ReadResponse, queue));
    }
}
