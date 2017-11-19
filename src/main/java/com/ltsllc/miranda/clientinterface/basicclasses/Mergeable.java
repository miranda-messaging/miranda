package com.ltsllc.miranda.clientinterface.basicclasses;

import com.ltsllc.common.util.ImprovedRandom;

/**
 * An Object that can be merged with another object.
 * <p>
 * <p>
 * Instances of this class implicitly have the notion of when they were last changed.
 * When merging with another instance of this class, the more recent version should
 * be used.
 * </p>
 */
public abstract class Mergeable {
    /**
     * Copy all attributes from another instance.
     * <p>
     * <p>
     * After invoking this method, this instance should be {@link #equals(Object)} equivalent to
     * the object passed to it.
     * </p>
     * <p>
     * <p>
     * The argument should be of the same class as this instance.
     * </p>
     * <p>
     * <p>
     * Any subclasses should copy their attributes.
     * </p>
     *
     * @param mergeable Another non-null instance that this object should copy from.  The object should be of the same
     *                  type as this instance --- casting to the type of the receiver should work.
     */
    abstract public void copyFrom(Mergeable mergeable);

    private Long lastChange;

    public Long getLastChange() {
        return lastChange;
    }

    public void setLastChange(Long lastChange) {
        this.lastChange = lastChange;
    }

    public boolean changedAfter(Mergeable other) {
        if (getLastChange() == null)
            return false;

        if (other.getLastChange() == null)
            return true;

        return getLastChange().longValue() > other.getLastChange().longValue();
    }

    public void initialize(ImprovedRandom random) {
        this.lastChange = random.nextNonNegativeLong();
    }

    public boolean merge(Mergeable other) {
        if (changedAfter(other))
            return false;

        if (other.getLastChange() != null)
            lastChange = new Long(other.getLastChange().longValue());

        copyFrom(other);

        return true;
    }
}
