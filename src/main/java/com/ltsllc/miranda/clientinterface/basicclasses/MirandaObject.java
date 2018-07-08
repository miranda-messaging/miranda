package com.ltsllc.miranda.clientinterface.basicclasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * An Object that knows how to merge itself with another object.
 * <p>
 * This class
 * </p>
 * <p>
 * <h3>Properties</h3>
 * <table border="1">
 * <th>
 * <td>Name</td>
 * <td>Type</td>
 * <td>Description</td>
 * </th>
 * <tr>
 * <td>lastChange</td>
 * <td>Long</td>
 * <td>The Java time when the object was last changed.</td>
 * </tr>
 * </table>
 */
abstract public class MirandaObject extends MergeableObject implements Equivalent {
    private static Gson gson;

    public static Gson getGson() {
        return gson;
    }


    public MirandaObject() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gson = gsonBuilder.create();
        }
    }

    /**
     * Are two strings equal?
     * <p>
     * This method is null safe --- one or both parameters can be null.
     * </p>
     *
     * @param s1 The first string to be compared.
     * @param s2 The second string to be copared.
     * @return true if the strings are equivalent, false otherwise.
     */
    public static boolean stringsAreEqual(String s1, String s2) {
        if (s1 == s2)
            return true;

        if (s1 == null || s2 == null)
            return false;

        return s1.equals(s2);
    }

    /**
     * Are two Long objects equivalent?
     * <p>
     * <p>
     * This method returns true if the two longs are == equivalent or
     * the long values they contain are == equivalent.
     * </p>
     *
     * @param l1 The first Long to compare
     * @param l2 The second Long to compare
     * @return True if the two objects are equivalent, false otherwise.
     */
    public static boolean longObjectsAreEquivalent(Long l1, Long l2) {
        if (l1 == l2)
            return true;
        else if (l1 == null || l2 == null)
            return false;
        else {
            return l1.longValue() == l2.longValue();
        }
    }

    /**
     * Are two byte arrays equivalent?
     * <p>
     * <p>
     * This method returns true if the two arrays are == equivelent or if
     * they both have the same length and contain the same values.
     * </p>
     *
     * @param a1 The first byte array to compare.
     * @param a2 The second byte array to compare.
     * @return True if the arrays are equivalent.  False otherwise.
     */
    public static boolean byteArraysAreEqual(byte[] a1, byte[] a2) {
        if (a1 == a2)
            return true;

        if ((a1 == null) || (a2 == null))
            return false;

        if (a1.length != a2.length)
            return false;

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i])
                return false;
        }

        return true;
    }

    public static boolean listsAreEqual (List l1, List l2) {
        if (l1 == l2)
            return true;

        if (l1.size() != l2.size())
            return false;

        for (int i = 0; i < l1.size(); i++) {
            Object o1 = l1.get(i);
            Object o2 = l2.get(i);

            if (!o1.equals(o2))
                return false;
        }

        return true;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public boolean merge (Mergeable mergable) {
        if (getLastChange() > mergable.getLastChange())
            return false;
        else {
            copyFrom(mergable);
            return true;
        }
    }


}
