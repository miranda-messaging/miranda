package com.ltsllc.miranda.file;

import com.ltsllc.miranda.TestCase;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.util.ImprovedRandom;
import com.ltsllc.miranda.util.file.EventFileCreator;
import com.ltsllc.miranda.util.file.FileCreator;
import com.ltsllc.miranda.util.file.RandomFileCreator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestDirectory extends TestCase {
    private static Logger logger = Logger.getLogger(TestDirectory.class);

    private Directory directory;

    public Directory getDirectory() {
        return directory;
    }

    public void reset () {
        super.reset();

        directory = null;
    }

    @Before
    public void setup () {
        reset();
        setuplog4j();
        setupMirandaProperties();

        MirandaProperties properties = MirandaProperties.getInstance();
        String directory = properties.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);
        this.directory = new SystemMessages(directory, getWriter());
    }

    @Test
    public void testConstructor () {
        MirandaProperties properties = MirandaProperties.getInstance();
        String dir = properties.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);

        assert (dir.equals(getDirectory().getFilename()));
        assert (getWriter() == getDirectory().getWriterQueue());
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

    public static boolean deleteFile (File file) {
        if (file.isDirectory())
            return false;

        if (!file.exists())
            return true;

        return file.delete();
    }

    public static boolean deleteDirectory (File directory) {
        if (!directory.isDirectory()) {
            return !directory.exists();
        }

        try {
            String[] contents = directory.list();

            for (String s : contents) {
                String fullname = directory.getCanonicalPath() + File.separator + s;
                File file = new File(fullname);
                if (file.isDirectory()) {
                    if (!deleteDirectory(file))
                        return false;
                } else {
                    if (!deleteFile(file))
                        return false;
                }
            }

            return directory.delete();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean contains (Object o, List list)
    {
        for (Object candidate : list) {
            if (candidate.equals(o))
                return true;
        }

        return false;
    }

    /**
     * Travese is used to identify files in the chosen directory.  Test to
     * see if it works and that it doesn't flag other files.
     *
     * @throws Exception
     */
    @Test
    public void testTraverse () throws Exception {
        File file = new File("testdir");

        try {
            assert (file.mkdirs());
            assert (createHierarchy(file, TRAVERSE_TEST));

            List<String> list = getDirectory().traverse(file.getName());
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

    public boolean createEventHiearchicy (String rootFilename, String[][] spec) {
        ImprovedRandom random = new ImprovedRandom();
        File root = new File(rootFilename);
        FileCreator randomFileCreator = new RandomFileCreator(1024, random);
        MirandaProperties properties = MirandaProperties.getInstance();
        int maxNumberOfEvents = 1 + properties.getIntegerProperty(MirandaProperties.PROPERTY_MESSAGE_FILE_SIZE);
        FileCreator eventFileCreator = new EventFileCreator(random, maxNumberOfEvents,1024);

        if (!root.isDirectory()) {
            if (root.exists())
                return false;

            if (!root.mkdirs())
                return false;

            for (String[] record : spec) {
                String fullName = rootFilename + File.separator + record[0];
                File file = new File(fullName);


                if ("directory".equalsIgnoreCase(record[1])) {
                    if (!file.isDirectory() && !file.mkdirs())
                        return false;
                } else if ("file".equalsIgnoreCase(record[1]) || "random file".equalsIgnoreCase(record[1])) {
                    randomFileCreator.createFile(file);
                } else if ("event file".equalsIgnoreCase(record[1])) {
                    eventFileCreator.createFile(file);
                }
            }
        }

        return true;
    }

    /**
     * load basically just does a {@link Directory#traverse(String, List)} to
     * locate files, and then {@link Directory#createMirandaFile(String)} to
     * set them up.  Test to see that it works correctly.
     */
    @Test
    public void testLoad () throws Exception {
        try {
            MirandaProperties properties = MirandaProperties.getInstance();
            properties.setProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY, "testdir");

            this.directory = new SystemMessages("testdir", getWriter());

            createEventHiearchicy("testdir", LOAD_SPEC);

            getDirectory().load();

            assert (getDirectory().getFiles().size() == 3);

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
            MirandaProperties properties = MirandaProperties.getInstance();
            properties.setProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY, "testdir");

            this.directory = new SystemMessages("testdir", getWriter());

            createEventHiearchicy("testdir", LOAD_SPEC);

            getDirectory().load();
            getDirectory().updateVersion();

            Version version = Version.createWithSha1(SHA1);

            assert (version.equals(getDirectory().getVersion()));
        } finally {
            deleteDirectory(root);
        }

    }
}
