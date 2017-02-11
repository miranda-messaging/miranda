package com.ltsllc.miranda.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * A collection of routines that are useful in processing MirandaProperties objects
 *
 * Created by Clark on 12/30/2016.
 */
public class PropertiesUtils {
    private static Logger logger = Logger.getLogger(PropertiesUtils.class);

    /**
     * Add the properties of p2 to p1.
     *
     * If a property is defined in both objects, use the version in p2.
     *
     * @param p1 The properties to augment.
     * @param p2 The properties to use to augment p1.
     */
    public static Properties augment (Properties p1, java.util.Properties p2) {
        for (String name : p2.stringPropertyNames()) {
            p1.setProperty(name, p2.getProperty(name));
        }

        return p1;
    }

    /**
     * Build a properties object from a String array.
     *
     * The spec is assumed to have the form name, value.  If the spec does
     * not have that format, then a {@link RuntimeException} may be thrown.
     *
     * @param spec See description
     * @return The properties object.  This may be empty but is not null.
     */
    public static Properties buildFrom (String[][] spec)
    {
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

    public static Properties setIfNull (Properties p1, Properties p2)
    {
        Set<String> names = p2.stringPropertyNames();

        for (String name : names)
        {
            if (p1.getProperty(name) == null) {
                p1.setProperty(name, p2.getProperty(name));
            }
        }

        return p1;
    }

    public static Integer parseIntOrDie(String name, String s) {
        Integer value = null;

        try {
            value = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            logger.fatal ("error parsing " + name + " (" + s + ")",e);
            System.exit(1);
        }

        return value;
    }

    public static void log (Properties p) {
        Object[] names = p.stringPropertyNames().toArray();
        Arrays.sort(names);

        for (Object o : names) {
            String name = (String) o;
            String value = p.getProperty(name);
            logger.info (name + " = " + value);
        }
    }


    public static Properties load(String filename) throws IOException {
        Properties properties = new Properties();
        File file = new File(filename);

        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);

            try {
                properties.load(fileInputStream);
            } finally {
                IOUtils.closeNoExceptions(fileInputStream);
            }
        }

        return properties;
    }

    public static int getIntProperty (Properties properties, String name) {
        String value = properties.getProperty(name);
        return Integer.parseInt(value);
    }

    public static int getIntProperty (String name)
    {
        return getIntProperty(System.getProperties(), name);
    }

    public static Properties merge (Properties p1, Properties p2) {
        for (String name : p2.stringPropertyNames())
        {
            if (null == p1.getProperty(name))
                p1.setProperty(name, p2.getProperty(name));
        }

        return p1;
    }
}
