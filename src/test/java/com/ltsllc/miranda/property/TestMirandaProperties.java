package com.ltsllc.miranda.property;

import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.servlet.objects.Property;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Clark on 2/26/2017.
 */
public class TestMirandaProperties extends TestCase {
    private MirandaProperties properties;
    private Properties startingSystemProperties;

    public Properties getStartingSystemProperties() {
        return startingSystemProperties;
    }

    public void setStartingSystemProperties(Properties startingSystemProperties) {
        this.startingSystemProperties = startingSystemProperties;
    }

    public MirandaProperties getProperties() {
        return properties;
    }

    public void reset () {
        super.reset();
    }

    public boolean hasDefaultValues (MirandaProperties properties) {
        Properties defaults = PropertiesUtils.buildFrom(MirandaProperties.DEFAULT_PROPERTIES);
        Properties p = PropertiesUtils.merge(System.getProperties(), defaults);
        Properties mirandaProperties = properties.getProperties();

        Properties p2 = PropertiesUtils.difference(mirandaProperties, p);
        return mirandaProperties.equals(p);
    }

    public static final String TEST_FILENAME = "test.properties";

    public void saveSystemProperties () {
        startingSystemProperties = PropertiesUtils.copy(System.getProperties());
    }

    public void restoreSystemProperties () {
        System.getProperties().clear();
        Properties p = System.getProperties();

        for (String name : startingSystemProperties.stringPropertyNames())
        {
            String value = startingSystemProperties.getProperty(name);
            p.put(name, value);
        }
    }

    @Before
    public void setup () {
        setuplog4j();
        saveSystemProperties();
        properties = new MirandaProperties(TEST_FILENAME, getMockWriter());
    }

    @After
    public void cleanup () {
        deleteFile(TEST_FILENAME);
        restoreSystemProperties();
    }

    public static final String TEST_VALUE = "wrong";

    public static final String OTHER_VALUE = "other";

    public static final String[] TEST_CONTENTS = {
            MirandaProperties.PROPERTY_USERS_FILE + " = " + TEST_VALUE
    };

    @Test
    public void testLoadTheDefaults () {
        //
        // just the defaults
        //
        touch(TEST_FILENAME);
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);

        getProperties().load(commandLine);

        assert (hasDefaultValues(getProperties()));
    }

    @Test
    public void testLoadWithSystemProperties () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);

        Properties system = System.getProperties();
        system.setProperty(MirandaProperties.PROPERTY_USERS_FILE, TEST_VALUE);

        getProperties().load(commandLine);

        assert (getProperties().getProperties().getProperty(MirandaProperties.PROPERTY_USERS_FILE).equals(TEST_VALUE));
    }

    @Test
    public void testLoadWithPropertiesFile () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);
        createFile(TEST_FILENAME, TEST_CONTENTS);

        getProperties().load(commandLine);

        assert (getProperties().getProperty(MirandaProperties.PROPERTY_USERS_FILE).equals(TEST_VALUE));
        assert (!MirandaProperties.DEFAULT_USERS_FILE.equals(TEST_VALUE));
    }

    @Test
    public void testLoadWithSomethingInThePropertiesFile () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);

        Properties system = System.getProperties();
        system.setProperty(MirandaProperties.PROPERTY_USERS_FILE, OTHER_VALUE);

        createFile(TEST_FILENAME, TEST_CONTENTS);

        getProperties().load(commandLine);

        assert (getProperties().getProperty(MirandaProperties.PROPERTY_USERS_FILE).equals(TEST_VALUE));
    }

    public static final String[] OTHER_CONTENTS = {
            MirandaProperties.PROPERTY_LOG4J_FILE + " = " + OTHER_VALUE
    };

    public static String TEST_LOG4J = "test_log4j";

    @Test
    public void testLoadWithValueOnCommandLine () {
        createFile(TEST_FILENAME, OTHER_CONTENTS);

        String[] argv = { TEST_FILENAME, "-log4j", TEST_LOG4J };
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);

        getProperties().load(commandLine);

        assert (getProperties().getProperty(MirandaProperties.PROPERTY_LOG4J_FILE).equals(TEST_LOG4J));
    }

    @Test
    public void testGetIntProperty () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);
        getProperties().load(commandLine);

        int panicLimit = getProperties().getIntProperty(MirandaProperties.PROPERTY_PANIC_LIMIT);
        assert (panicLimit == 3);

        int port = getProperties().getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        assert (port == 6789);

        int undefined = getProperties().getIntProperty("doesNotExist");
        assert (undefined == 0);
    }

    @Test
    public void testGetEncryptionMode () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);
        getProperties().load(commandLine);

        MirandaProperties.EncryptionModes mode = getProperties().getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);

        assert (mode == MirandaProperties.EncryptionModes.LocalCA);

        mode = getProperties().getEncryptionModeProperty("doesNotExist");

        assert (mode == MirandaProperties.EncryptionModes.Unknown);
    }

    @Test
    public void testGetNetworkProperty () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);
        getProperties().load(commandLine);

        MirandaProperties.Networks network = getProperties().getNetworkProperty();

        assert (network == MirandaProperties.Networks.Mina);

        network = getProperties().getNetworkProperty("doesNotExist");

        assert (network == MirandaProperties.Networks.Unknown);
    }

    public boolean listsAreEquivalent (List<Property> l1, List<Property> l2) {
        if (l1.size() != l2.size())
            return false;

        for (Property property : l1) {
            boolean noMatchFound = true;

            for (Property property2 : l2) {
                if (property.equals(property2)) {
                    noMatchFound = false;
                    break;
                }
            }

            if (noMatchFound)
                return false;
        }

        return true;
    }

    public List<Property> difference (List<Property> l1, List<Property> l2) {
        List<Property> difference = new ArrayList<Property>();

        for (Property property : l1) {
            boolean noMatchFound = true;

            for (Property property2 : l2) {
                if (property.equals(property2)) {
                    noMatchFound = false;
                    break;
                }
            }

            if (noMatchFound)
                difference.add(property);
        }

        return difference;
    }

    @Test
    public void testAsPropertyList () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);
        getProperties().load(commandLine);

        List<Property> list = getProperties().asPropertyList();

        Properties defaults = PropertiesUtils.buildFrom(MirandaProperties.DEFAULT_PROPERTIES);
        Properties systemPlusDefaults = PropertiesUtils.merge(System.getProperties(), defaults);
        List<Property> systemPlusDefaultsList = PropertiesUtils.toPropertyList(systemPlusDefaults);

        assert (listsAreEquivalent(list, systemPlusDefaultsList));
    }

    @Test
    public void testWrite () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);
        getProperties().load(commandLine);
    }
}
