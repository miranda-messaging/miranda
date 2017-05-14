package com.ltsllc.miranda;

import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 3/28/2017.
 */
public class TestVersion extends TestCase {
    private Version version;

    public Version getVersion() {
        return version;
    }

    public void reset () {
        super.reset();

        version = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        version = new Version();
    }

    @After
    public void cleanup () {
        deleteFile(TEST_FILENAME);
    }

    public static final String TEST_STRING = "whatever";
    public static final String TEST_SHA1 = "D869DB7FE62FB07C25A0403ECAEA55031744B5FB";

    @Test
    public void testConsrutctor () {
        try {
            version = new Version(TEST_STRING);
            assert (version.getSha1().equals(TEST_SHA1));            version = version;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String TEST_FILENAME = "testfile";
    public static String TEST_BINARY_SHA1 = "E82C04D32DA09DC7F41581A5922E342B194FC67089A4A29345BB3B7C42D4DC91";

    @Test
    public void testConstructor2 () {
        setuplog4j();
        createFile(TEST_FILENAME, TEST_STRING.getBytes());
        LinkedBlockingQueue<Message> temp = new LinkedBlockingQueue<Message>();
        ClusterFile clusterFile = new ClusterFile(TEST_FILENAME, null, null, temp);
        try {
            Version version = new Version(clusterFile);
            assert (version.getSha1().equals(TEST_BINARY_SHA1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEquals () {
        getVersion().setSha1(TEST_BINARY_SHA1);
        assert (getVersion().equals(getVersion()));
        assert (!getVersion().equals(null));
    }
}
