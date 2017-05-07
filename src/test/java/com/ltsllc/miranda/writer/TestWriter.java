package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestWriter extends TestCase {
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

    @Test
    public void testWrite () {
        try {
            getWriter().write(TEST_FILE_NAME, TEST_DATA);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert (fileIsEquivalentTo(TEST_FILE_NAME, TEST_DATA));
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
