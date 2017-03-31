package com.ltsllc.miranda.session;

import com.ltsllc.miranda.MirandaException;

/**
 * The session id is unknown.
 */
public class UnknownSession extends MirandaException {
    private long id;

    public long getId() {
        return id;
    }

    public UnknownSession (long id) {
        this.id = id;
    }
}
