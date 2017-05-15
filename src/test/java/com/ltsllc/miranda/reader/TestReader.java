package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.PrivateKey;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.security.GeneralSecurityException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestReader extends TestCase {
    @Mock
    private PrivateKey mockPrivateKey;

    private Reader reader;

    public Reader getReader() {
        return reader;
    }

    public PrivateKey getMockPrivateKey() {
        return mockPrivateKey;
    }

    public void reset () {
        super.reset();

        mockPrivateKey = null;
        reader = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockPrivateKey = mock(PrivateKey.class);
        reader = new Reader(mockPrivateKey);
    }

    public static final String TEST_FILENAME = "testfile";
    public static final String TEST_FILE_CONTENTS = "whatever";

    @Test
    public void testReadSuccess () {
        GeneralSecurityException generalSecurityException = null;
        Reader.ReadResult result = null;

        String hexString = Utils.bytesToString(TEST_FILE_CONTENTS.getBytes());
        createFile(TEST_FILENAME, hexString);
        try {
            when(getMockPrivateKey().decrypt(Matchers.any(byte[].class))).thenReturn(TEST_FILE_CONTENTS.getBytes());
            result = getReader().read(TEST_FILENAME);
        } catch (GeneralSecurityException e) {
            generalSecurityException = e;
        }

        assert (result.result == Results.Success);
        assert (arraysAreEquivalent(result.data, TEST_FILE_CONTENTS.getBytes()));
        assert (null == generalSecurityException);
    }

    @Test
    public void testReadFileDoesNotExist () {
        Reader.ReadResult result = getReader().read("I don't exist");

        assert (result.result == Results.FileNotFound);
    }

    @Test
    public void testReadGeneralSecurityException () {
        GeneralSecurityException generalSecurityException = null;
        Reader.ReadResult result = null;

        String hexString = Utils.bytesToString(TEST_FILE_CONTENTS.getBytes());
        createFile(TEST_FILENAME, hexString);
        try {
            GeneralSecurityException exception = new GeneralSecurityException("a test");
            when(getMockPrivateKey().decrypt(Matchers.any(byte[].class))).thenThrow(exception);
            result = getReader().read(TEST_FILENAME);
        } catch (GeneralSecurityException e) {
            generalSecurityException = e;
        }

        assert (result.result == Results.Exception);
    }

    @Test
    public void testSendRead () {
        getReader().sendReadMessage(null, this, "whatever");

        assert (contains(Message.Subjects.Read, getReader().getQueue()));
    }
}
