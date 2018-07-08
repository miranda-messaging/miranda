package com.ltsllc.miranda.clientinterface.basicclasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.commons.util.ImprovedRandom;

/**
 * An Object that can be merged with another object.
 * <p>
 * <p>
 * Instances of this class implicitly have the notion of when they were last changed.
 * When merging with another instance of this class, the more recent version should
 * be used.
 * </p>
 */
public abstract class MergeableObject implements Mergeable {
    /**
     * Copy all attributes from another instance.
     * <p>
     * After invoking this method, this instance should be {@link #equals(Object)} equivalent to
     * the object passed to it.
     * </p>
     * <p>
     * The argument should be of the same class as this instance.
     * </p>
     * <p>
     * Any subclasses should copy their attributes.
     * </p>
     *
     * @param mergeable Another non-null instance that this object should copy from.  The object should be of the same
     *                  type as this instance --- casting to the type of the receiver should work.
     */
    abstract public void copyFrom(MergeableObject mergeable);

    private static Gson gson;

    private long lastChange;

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gson = gsonBuilder.create();
        }

        return gson;
    }

    public static void setGson(Gson gson) {
        MergeableObject.gson = gson;
    }

    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(Long lastChange) {
        this.lastChange = lastChange;
    }

    public boolean changedAfter(MergeableObject other) {
        if (getLastChange() == -1)
            return false;

        if (other.getLastChange() == -1)
            return true;

        return getLastChange() > other.getLastChange();
    }

    public void initialize(ImprovedRandom random) {
        this.lastChange = random.nextNonNegativeLong();
    }

    public boolean merge(MergeableObject other) {
        if (changedAfter(other))
            return false;

        if (other.getLastChange() != -1)
            lastChange = new Long(other.getLastChange());

        copyFrom(other);

        return true;
    }

    public String toJson () {
        return getGson().toJson(this);
    }
}
