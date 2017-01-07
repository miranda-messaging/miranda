package com.ltsllc.miranda.util;

import java.util.Properties;

/**
 * A collection of routines that are useful in processing MirandaProperties objects
 *
 * Created by Clark on 12/30/2016.
 */
public class PropertiesUtils {
    /**
     * Add the properties of p2 to p1.
     *
     * If a property is defined in both objects, use the version in p2.
     *
     * @param p1 The properties to augment.
     * @param p2 The properties to use to augment p1.
     */
    public static void augment (java.util.Properties p1, java.util.Properties p2) {
        for (String name : p2.stringPropertyNames()) {
            p1.setProperty(name, p2.getProperty(name));
        }
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
}
