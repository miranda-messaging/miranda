package com.ltsllc.miranda.property;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.FileChangedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/19/2017.
 */
public class TestMirandaPropertiesReadyState extends TestCase {
    @Mock
    private MirandaProperties mockProperties;

    @Mock
    private Miranda mockMiranda;

    private MirandaPropertiesReadyState readyState;

    public MirandaPropertiesReadyState getReadyState() {
        return readyState;
    }

    public MirandaProperties getMockProperties() {
        return mockProperties;
    }

    public Miranda getMockMiranda() {
        return mockMiranda;
    }

    public void reset () {
        super.reset();

        mockMiranda = null;
        mockProperties = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockMiranda = mock(Miranda.class);
        Miranda.setInstance(mockMiranda);

        mockProperties = mock(MirandaProperties.class);
        readyState = new MirandaPropertiesReadyState(mockProperties);
    }

    @Test
    public void testProcessWriteSucceeded () {
        WriteSucceededMessage writeSucceededMessage = new WriteSucceededMessage(null, "whatever", this);

        State nextState = getReadyState().processMessage(writeSucceededMessage);

        assert (nextState instanceof MirandaPropertiesReadyState);
    }

    @Test
    public void testProcessWriteFailed () {
        WriteFailedMessage writeFailedMessage = new WriteFailedMessage(null, "whatever", new IOException(),this);

        when(getMockProperties().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(writeFailedMessage);

        assert (nextState instanceof MirandaPropertiesReadyState);
        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    @Test
    public void testProcessFileChangedMessage () {
        File file = new File(MirandaProperties.DEFAULT_PROPERTIES_FILENAME);
        FileChangedMessage fileChangedMessage = new FileChangedMessage(null, this, file);

        when(getMockProperties().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(fileChangedMessage);

        assert (nextState instanceof MirandaPropertiesReadyState);
        verify(getMockMiranda(), atLeastOnce()).sendNewProperties(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.any(MirandaProperties.class));
    }
}
