package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.State;

/**
 * Miranda is shutting down.  When it enters this state, it is waiting for
 * a ready message from each of its subsystems.  When it gets those it terminates.
 */
public class ShuttingDownState extends State {
    public Miranda getMiranda () {
        return (Miranda) getContainer();
    }

    public ShuttingDownState (Miranda miranda) {
        super(miranda);
    }
}
