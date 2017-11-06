package com.ltsllc.miranda.actions;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * A class that "knows" how to join a cluster.
 */
public class JoinClusterState extends State {
    /**
     * Create a new instance.
     *
     * <p>
     *     The object passed to the constructor must be non-null.
     * </p>
     *
     * @param joinClusterAction The container for the State
     */
    public JoinClusterState (JoinClusterAction joinClusterAction) throws MirandaException {
        super(joinClusterAction);
    }
}
