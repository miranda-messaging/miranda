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

import com.google.gson.Gson;
import com.ltsllc.miranda.EncryptedMessage;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.PrivateKey;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.IOException;
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

    public void reset() {
        super.reset();

        mockPrivateKey = null;
        reader = null;
    }

    @Before
    public void setup() {
        reset();

        super.setup();

        setuplog4j();
        mockPrivateKey = mock(PrivateKey.class);
        reader = new Reader(mockPrivateKey);
    }

    @After
    public void cleanup () {
        System.gc();
        deleteFile(TEST_FILENAME);
    }

    public static final String TEST_FILENAME = "testfile";
    public static final String TEST_FILE_CONTENTS = "whatever";
    public static final String TEST_STRING = "hi there";

    public void createTestFile (String filename) {
        EncryptedMessage encryptedMessage = new EncryptedMessage();
        encryptedMessage.setMessage(TEST_STRING);
        encryptedMessage.setKey(TEST_STRING);
        encryptedMessage.setLength(TEST_STRING.length());
        Gson gson = new Gson();
        String json = gson.toJson(encryptedMessage);
        String hexString = Utils.bytesToString(json.getBytes());
        createFile(filename, hexString);
    }

    @Test
    public void testReadSuccess() throws GeneralSecurityException, IOException {
        GeneralSecurityException generalSecurityException = null;
        Reader.ReadResult result = null;

        createTestFile(TEST_FILENAME);

        when(getMockPrivateKey().decrypt(Matchers.any(EncryptedMessage.class))).thenReturn(TEST_FILE_CONTENTS.getBytes());

        result = getReader().read(TEST_FILENAME);

        assert (result.result == Results.Success);
        assert (arraysAreEquivalent(result.data, TEST_FILE_CONTENTS.getBytes()));
        assert (null == generalSecurityException);
    }

    @Test
    public void testReadFileDoesNotExist() throws GeneralSecurityException, IOException {
        Reader.ReadResult result = getReader().read("I don't exist");

        assert (result.result == Results.FileNotFound);
    }

    @Test
    public void testReadGeneralSecurityException() {
        Exception exception = null;
        Reader.ReadResult result = null;

        createTestFile(TEST_FILENAME);

        try {
            GeneralSecurityException generalSecurityException = new GeneralSecurityException("a test");
            when(getMockPrivateKey().decrypt(Matchers.any(EncryptedMessage.class))).thenThrow(generalSecurityException);
            result = getReader().read(TEST_FILENAME);
        } catch (GeneralSecurityException | IOException e) {
            exception = e;
        }

        assert (exception != null);
    }

    @Test
    public void testSendRead() {
        getReader().sendReadMessage(null, this, "whatever");

        assert (contains(Message.Subjects.Read, getReader().getQueue()));
    }
}
