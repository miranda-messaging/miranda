package com.ltsllc.miranda.file;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestDirectory extends TestCase {
    private static Logger logger = Logger.getLogger(TestDirectory.class);

    private Directory directory;

    @Mock
    private MirandaFile mockMirandaFile;

    public Directory getDirectory() {
        return directory;
    }

    public MirandaFile getMockMirandaFile() {
        return mockMirandaFile;
    }

    public void reset () {
        super.reset();

        directory = null;
        mockMirandaFile = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupWriter();
        setupMirandaProperties();

        MirandaProperties properties = Miranda.properties;
        String directory = properties.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);
        this.directory = new SystemMessages(directory, getMockWriter());

        this.mockMirandaFile = mock(MirandaFile.class);

        List<MirandaFile> fileList = new ArrayList<MirandaFile>();
        fileList.add(getMockMirandaFile());
        this.directory.setFiles(fileList);
    }

    @After
    public void cleanup () {
        deleteDirectory("testdir");
    }

    @Test
    public void testConstructor () {
        MirandaProperties properties = Miranda.properties;
        String dir = properties.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);

        assert (dir.equals(getDirectory().getFilename()));
    }

    private String[][] TRAVERSE_TEST = {
            {"whatever", "file"},
            {"20170220-003.msg", "file"},
            {"old", "directory"},
            {"old/whatever", "file"},
            {"new", "directory"},
            {"new/20170122-001.msg", "file"},
            {"new/20170123-002.msg", "file"}
    };

    public boolean createHierarchy(File root, String[][] spec) {
        SecureRandom random = new SecureRandom();

        for (String[] record : spec) {
            File file = new File(root.getName() + File.separator + record[0]);

            if (record[1].equalsIgnoreCase("directory")) {
                if (!file.mkdirs()) {
                    return false;
                }
            } else if (record[1].equalsIgnoreCase("file")) {
                if (!createFile(file, 1024, random))
                    return false;
            }
        }

        return true;
    }

    private static final String TEST_DIR = "testdir";

    public void setupFileSystem () {
        File file = new File(TEST_DIR);

        assert (file.mkdirs());
        assert (createHierarchy(file, TRAVERSE_TEST));
    }

    /**
     * Travese is used to identify files in the chosen directory.  Test to
     * see if it works and that it doesn't flag other files.
     *
     * @throws Exception
     */
    @Test
    public void testTraverse () throws Exception {
        File file = new File(TEST_DIR);

        try {
            setupFileSystem();
            List<String> list = getDirectory().traverse(TEST_DIR);
            assert (list.size() == 3);
            assert (contains("testdir\\20170220-003.msg", list));
            assert (contains("testdir\\new\\20170122-001.msg", list));
            assert (contains("testdir\\new\\20170123-002.msg", list));
            assert (!contains("testdir\\whatever", list));
            assert (!contains("testdir\\old\\whatever", list));
        } finally {
            deleteDirectory(file);
        }
    }

    private static final String[][] LOAD_SPEC = {
            {"whatever", "random file"},
            {"20170220-003.msg", "event file"},
            {"old", "directory"},
            {"old/whatever", "random file"},
            {"new", "directory"},
            {"new/20170122-001.msg", "event file"},
            {"new/20170123-002.msg", "event file"}
    };

    /**
     * load basically just does a {@link Directory#traverse(String, List)} to
     * locate files, and then {@link Directory#createMirandaFile(String)} to
     * set them up.  Test to see that it works correctly.
     */
    @Test
    public void testLoad () throws Exception {
        try {
            MirandaProperties properties = Miranda.properties;
            properties.setProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY, "testdir");

            pause (250);

            this.directory = new SystemMessages("testdir", Writer.getInstance());
            assert (createFileSystem("testdir", LOAD_SPEC));

            pause (250);

            getDirectory().load();

            pause( 250);

            int directorySize = getDirectory().getFiles().size();
            getLogger().info ("directory size = " + directorySize);
            assert (directorySize == 3);
        } finally {
            File root = new File("testdir");
            deleteDirectory(root);
        }
    }

    public static final String SHA1 = "25D89D8946AEA804D9838DAE6EA2F24769C804C9";

    /**
     * {@link Directory#updateVersion()} should be a sha1 of the sha1s of all
     * the message files.
     */
    @Test
    public void testUpdateVersion () {
        File root = new File("testdir");

        try {
            MirandaProperties properties = Miranda.properties;
            properties.setProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY, "testdir");

            this.directory = new SystemMessages("testdir", Writer.getInstance());

            createFileSystem("testdir", LOAD_SPEC);

            getDirectory().load();
            getDirectory().updateVersion();

            Version version = Version.createWithSha1(SHA1);

            assert (version.equals(getDirectory().getVersion()));
        } finally {
            deleteDirectory(root);
        }
    }

    @Test
    public void testWrite () {
        getDirectory().write();

        verify(getMockMirandaFile(), atLeastOnce()).write();
    }
}
