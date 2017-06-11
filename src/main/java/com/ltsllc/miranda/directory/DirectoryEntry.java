package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.file.Directory;

/**
 * A file in a {@link Directory}.
 */
public interface DirectoryEntry {
    /**
     * The key for the file.
     *
     * If two instances return the same key, {@link DirectoryEntry#isEquivalentTo(DirectoryEntry)}
     * should also return <code>true</code> when called with the other instance.
     *
     * @return A String key for this instance.
     */
    public String getKey();

    /**
     * Is this instance equivalent to another instance?
     *
     * @param other The other instance.
     * @return <code>true</code> if the two instances are equivalent, <code>false</code>
     * otherwise.
     */
    public boolean isEquivalentTo(DirectoryEntry other);
}
