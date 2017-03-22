package com.ltsllc.miranda.util;

import com.ltsllc.miranda.servlet.objects.Property;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * A collection of routines that are useful in processing MirandaProperties objects
 * <p>
 * Created by Clark on 12/30/2016.
 */
public class PropertiesUtils {
    private static Logger logger = Logger.getLogger(PropertiesUtils.class);

    /**
     * Add the properties of p2 to p1.
     * <p>
     * If a property is defined in both objects, use the version in p2.
     *
     * @param p1 The properties to augment.
     * @param p2 The properties to use to augment p1.
     */
    public static Properties augment(Properties p1, java.util.Properties p2) {
        for (String name : p2.stringPropertyNames()) {
            p1.setProperty(name, p2.getProperty(name));
        }

        return p1;
    }

    /**
     * Build a properties object from a String array.
     * <p>
     * The spec is assumed to have the form name, value.  If the spec does
     * not have that format, then a {@link RuntimeException} may be thrown.
     *
     * @param spec See description
     * @return The properties object.  This may be empty but is not null.
     */
    public static Properties buildFrom(String[][] spec) {
        Properties p = new Properties();

        if (spec != null) {
            for (String[] line : spec) {
                String name = line[0];
                String value = line[1];

                p.setProperty(name, value);
            }
        }

        return p;
    }

    public static Properties setIfNull(Properties p1, Properties p2) {
        Set<String> names = p2.stringPropertyNames();

        for (String name : names) {
            if (p1.getProperty(name) == null) {
                p1.setProperty(name, p2.getProperty(name));
            }
        }

        return p1;
    }

    public static void log(Properties p) {
        Object[] names = p.stringPropertyNames().toArray();
        Arrays.sort(names);

        for (Object o : names) {
            String name = (String) o;
            String value = p.getProperty(name);
            logger.info(name + " = " + value);
        }
    }


    /**
     * Load a properties file, if it exits.
     *
     * @param filename The file
     * @return The properties object.  If the file exists, this will be the
     * properties contained in the file.  Otherwise, this will be empty.
     */
    public static Properties load(String filename) {
        Properties properties = new Properties();
        FileInputStream fileInputStream = null;

        try {
            File file = new File(filename);

            if (file.exists()) {
                fileInputStream = new FileInputStream(file);

                try {
                    properties.load(fileInputStream);
                } finally {
                    IOUtils.closeNoExceptions(fileInputStream);
                }
            }
        } catch (IOException e) {
            logger.fatal("Exception while trying to load properties from : " + filename, e);
            System.exit(1);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return properties;
    }

    public static int getIntProperty(Properties properties, String name) {
        String value = properties.getProperty(name);
        return Integer.parseInt(value);
    }

    public static int getIntProperty(String name) {
        return getIntProperty(System.getProperties(), name);
    }

    public static Properties merge(Properties p1, Properties p2) {
        for (String name : p2.stringPropertyNames()) {
            if (null == p1.getProperty(name))
                p1.setProperty(name, p2.getProperty(name));
        }

        return p1;
    }

    /**
     * Replace the properties in the first argument, with the second.
     * <p>
     * Thatis, if a property exists only in p1, then take p1's version.  If a
     * property only exists in p2, then take p2's version.  If a property
     * exists in both, then take p2's version.
     * </p>
     *
     * @param p1 See above.
     * @param p2 See above.
     * @return See above.
     */
    public static Properties overwrite(Properties p1, Properties p2) {
        for (String name : p2.stringPropertyNames()) {
            String value = p2.getProperty(name);
            p1.setProperty(name, value);
        }

        return p1;
    }

    /**
     * Return a copy of the {@link Properties} object passed to it --- changes
     * to it will <b>not</b> affect the original.
     */
    public static Properties copy (Properties original) {
        Properties copy = new Properties();

        if (null != original) {
            for (String name : original.stringPropertyNames()) {
                String value = original.getProperty(name);
                copy.setProperty(name, value);
            }
        }

        return copy;
    }

    /**
     * Return a new {@link Properties} object that contains those properties
     * that are unique to p1.
     */
    public static Properties difference (Properties p1, Properties p2) {
        Properties result = copy(p1);
        for (Object o : p2.keySet()) {
            if (result.keySet().contains(o))
                result.remove(o);
        }

        return result;
    }

    public static List<Property> buildPropertyList (String[][] spec) {
        List<Property> list = new ArrayList<Property>();
        for (String[] line : spec) {
            String name = line.length > 0 ? line[0] : "unknown";
            String value = line.length > 1 ? line[1] : "unknown";
            Property property = new Property(name, value);
            list.add(property);
        }

        return list;
    }

    public static List<Property> toPropertyList (Properties properties) {
        List<Property> list = new ArrayList<Property>(properties.size());
        for (String name : properties.stringPropertyNames())
        {
            String value = properties.getProperty(name);
            Property property = new Property(name, value);
            list.add(property);
        }

        return list;
    }
}
