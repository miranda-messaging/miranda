package com.ltsllc.miranda.topics;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.topics.states.TopicsFileReadyState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestTopicsFileReadyState extends TestCase {
    @Mock
    private TopicsFile mockTopicsFile;

    private TopicsFileReadyState readyState;

    public TopicsFileReadyState getReadyState() {
        return readyState;
    }

    @Override
    public TopicsFile getMockTopicsFile() {
        return mockTopicsFile;
    }

    public void reset () {
        super.reset();

        mockTopicsFile = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockTopicsFile = mock(TopicsFile.class);
        readyState = new TopicsFileReadyState(mockTopicsFile);
    }

    @Test
    public void testProcessGetVersion () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetVersionMessage getVersionMessage = new GetVersionMessage(null, this, queue);

        State nextState = getReadyState().processMessage(getVersionMessage);

        assert (nextState instanceof TopicsFileReadyState);
        assert (contains(Message.Subjects.Version, queue));
    }
}
