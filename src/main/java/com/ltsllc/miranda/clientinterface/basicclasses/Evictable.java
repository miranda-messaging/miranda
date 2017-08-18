package com.ltsllc.miranda.clientinterface.basicclasses;

/**
 * An object that can be removed from memory.
 *
 * <p>
 *     Classes that implement this interface can be removed to make space for other objects.
 *     An instance signals that it can be removed by returning true to {@link #canBeEvicted()}.
 * </p>
 */
public interface Evictable {
    /**
     * Answer true if this object can be evicted.
     *
     * @return
     */
    public boolean canBeEvicted ();
}
