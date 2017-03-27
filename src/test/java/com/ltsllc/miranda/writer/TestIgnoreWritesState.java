package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestIgnoreWritesState extends TestCase {
    private IgnoreWritesState  ignoreWritesState;

    public IgnoreWritesState getIgnoreWritesState() {
        return ignoreWritesState;
    }

    public void reset () {
        super.reset();

        ignoreWritesState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        ignoreWritesState = new IgnoreWritesState(getMockWriter());
    }

    public static byte[] TEST_DATA = {1, 2, 3, 4};

    @Test
    public void testProcessWriteMessage () {
        setuplog4j();
        IgnoreWritesState.setLogger(getMockLogger());
        WriteMessage writeMessage = new WriteMessage("whatever", TEST_DATA, null, this);

        State nextState = getIgnoreWritesState().processMessage(writeMessage);

        assert (nextState instanceof IgnoreWritesState);
        verify (getMockLogger(), atLeastOnce()).warn(Matchers.anyString());
    }
}
