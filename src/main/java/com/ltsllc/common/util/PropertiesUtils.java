/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.common.util;

import java.io.*;
import java.util.*;

/**
 * A collection of routines that are useful in processing MirandaProperties objects
 * <p>
 * Created by Clark on 12/30/2016.
 */
public class PropertiesUtils {
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

    /**
     * Log the names and values of a properties object to the system log.
     *
     * <p>
     *     This method lists the property names and values of the properties
     *     object passed to this method to the system log.  The properties
     *     are sorted by name before printing.
     * </p>
     *
     * @param p The properties object to log.
     */
    public static String toString (Properties p) {
        Object[] names = p.stringPropertyNames().toArray();
        Arrays.sort(names);

        PrintWriter printWriter = null;

        try {
            StringWriter stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);

            for (int i = 0; i < names.length; i++) {
                String name = (String) names[i];
                String value = p.getProperty(name);

                printWriter.println(name + " = " + value);
            }

            return stringWriter.toString();
        } finally {
            Utils.closeIgnoreExceptions(printWriter);
        }
    }

    /**
     * Load a properties file, if it exits.
     *
     * @param filename The file
     * @return The properties object.  If the file exists, this will be the
     * properties contained in the file.  Otherwise, this will be empty.
     */
    public static Properties load(String filename) throws IOException {
        Properties properties = new Properties();

        FileInputStream fileInputStream = null;

        try {
            File file = new File(filename);

            if (file.exists()) {
                fileInputStream = new FileInputStream(file);

                try {
                    properties.load(fileInputStream);
                } finally {
                    Utils.closeIgnoreExceptions(fileInputStream);
                }
            }
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return properties;
    }

    /**
     * Merge one set of properties with another set and return the merged set.
     *
     * <p>
     *     This method modifies the first set passed to it and returns it.
     * </p>
     *
     * <p>
     *     For each property in p2, there are two possibilities: it is not
     *     defined in p1 or it is.  In the case where it is not defined, the
     *     property is added. When it is defined, use the one defined by p1.
     * </p>
     *
     * @param p1 The first set of properties.
     * @param p2 The second set of properties.
     * @return p1 modified as outlined in the description.
     */
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
     * That is, if a property exists only in p1, then take p1's version.  If a
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
     * that are unique to p1 or p2.
     */
    public static Properties difference (Properties p1, Properties p2) {
        Properties result = copy(p1);
        for (Object o : p2.keySet()) {
            if (result.keySet().contains(o))
                result.remove(o);
            else
                result.put(o, p2.get(o));
        }

        return result;
    }

    /**
     * Return a list of Property objects that are the properties passed to
     * this method.
     *
     * @param properties The properties to convert.
     * @return See above.
     */
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

    public static Properties mapToProperties (Map<String, String> map) {
        Properties properties = new Properties();

        for (String key : map.keySet()) {
            String value = map.get(key);
            properties.setProperty(key, value);
        }

        return properties;
    }
}
