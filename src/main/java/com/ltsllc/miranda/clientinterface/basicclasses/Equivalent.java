package com.ltsllc.miranda.clientinterface.basicclasses;

/**
 * Classes that implement this interface have the ability to determine if they refer
 * to the same thing.
 * <p>
 * If an instance of this interface returns true when {@link #isEquivalentTo(Object)} is
 * called, then they refer to the same thing.  One may be a more recent version of
 * the thing.
 * </p>
 * <p>
 * <p>
 * Another way of looking at it is two objects can be "primary key equivalent" but not
 * {@link #equals(Object)} equivalent.
 * </p>
 */
public interface Equivalent {
    /**
     * Return true if the receiver refers to the same thing as the argument.
     * <p>
     * <p>
     * Two instances are equivalent if
     * </p>
     * <ul>
     * <li>The have the same class</li>
     * <li>Their "primary keys" are the same</li>
     * </ul>
     * <p>
     * <p>
     * For example, if two users have the same user name, they are equivalent, but if
     * the user has changed their email address then equals will return false.
     * </p>
     *
     * @param o The other instance to compare against.
     * @return True if the other instance is equivalent to the receiver, false otherwise.
     */
    public boolean isEquivalentTo(Object o);
}
