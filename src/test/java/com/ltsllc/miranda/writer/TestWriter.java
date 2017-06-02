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

import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestWriter extends TestCase {
    private static Logger logger;

    private Writer writer;

    public Writer getWriter() {
        return writer;
    }

    public void reset () {
        super.reset();

        writer = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        writer = new Writer(getMockPublicKey());
    }

    @After
    public void cleanup () {
        String filename = TEST_FILE_NAME + ".backup";
        deleteFile(TEST_FILE_NAME);
        deleteFile(filename);
    }

    @Test
    public void testConstructor () {
        assert (getWriter().getQueue() != null);
        assert (getWriter().getCurrentState() instanceof WriterReadyState);
        assert (Writer.getInstance() != null);
    }

    public static final String TEST_FILE_NAME = "testfile";
    public static final byte[] TEST_DATA = { 1, 2, 3, 4 };

    public byte[] readFile (String filename) {
        byte[] fileData = null;
        FileInputStream fileInputStream = null;

        try {
            File file = new File(filename);
            int length = (int) file.length();
            fileData = new byte[length];
            fileInputStream = new FileInputStream(TEST_FILE_NAME);
            fileInputStream.read(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return fileData;
    }

    public boolean equivalent (byte[] b1, byte[] b2) {
        if (b1.length != b2.length)
            return false;

        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i])
                return false;
        }

        return true;
    }

    public boolean fileIsEquivalentTo (String filename, byte[] data) {
        byte[] fileData = readFile(filename);
        return equivalent(fileData, data);
    }

    public boolean fileIsEquivalentTo (String filename, String hexString) {
        try {
            byte[] data = Utils.hexStringToBytes(hexString);
            return fileIsEquivalentTo(filename, data);
        } catch (Exception e) {
            System.err.println ("Exception");
            e.printStackTrace();
        }

        return false;
    }

    public static final String TEST_CIPHER_TEXT = "4123983514D0DB213E5CE7AEBAF3BBD4DB78676D74B2F7AD5885818773F2467335F0671CD039B35A732BDE66DA2FFF93DFA62CFACBD5B6AC2A900E1C8E0C4C1BD31B2D0A5BA2F476F157100EDDF8C9BF62971AF0213FEBA1125C3622A15B872111D1D817AF5DD500D7B59405CC62CD5065AF1C5CB227133B7D29A11AD9C4DA7EC2BDAF6EE0A23C694D780068FA74D20081BFF4C77B449433E79920B2184796D40CEF972BA3794E060AB4BCCD36B0463621215B6672D0497CE835F60CBAD04B613C000E62ED9C402709DB83A5AA28E2ACE3CF701168B02A09C87CE02060E42B48E7EC2B78F975C9A9F1CB68940C7B7686A9A82DE9567031003C3E76A2EC6F5EEB";

    @Test
    public void testWrite () {
        try {
            when(getMockPublicKey().encrypt(Matchers.any(byte[].class))).thenReturn(Utils.hexStringToBytes(TEST_CIPHER_TEXT));
        } catch (Exception e) {
            System.err.println("Exception");
            e.printStackTrace();
        }

        try {
            getWriter().write(TEST_FILE_NAME, TEST_DATA);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert (fileIsEquivalentTo(TEST_FILE_NAME, TEST_CIPHER_TEXT));
    }

    public static final String TEST_FILE_CONTENTS = "01020304";

    @Test
    public void testBackupSuccess () {
        try {
            createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);
            File file = new File(TEST_FILE_NAME);
            getWriter().backup(file);

            file = new File (TEST_FILE_NAME + ".backup");
            assert (file.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileInputStream openFile (String filename) {
        try {
            return new FileInputStream(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Test
    public void testBackupCannotDeleteBackup () {
        FileInputStream fileInputStream = null;

        try {
            String filename = TEST_FILE_NAME + ".backup";
            createFile(filename, TEST_FILE_CONTENTS);
            createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);
            fileInputStream = openFile(filename);
            IOException ioException = null;
            try {
                File file = new File(TEST_FILE_NAME);
                getWriter().backup(file);
            } catch (IOException e) {
                ioException = e;
            }

            assert (ioException != null);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }
    }
}
