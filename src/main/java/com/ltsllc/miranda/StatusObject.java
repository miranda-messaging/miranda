package com.ltsllc.miranda;

/**
 * Created by Clark on 2/19/2017.
 */

import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Updateable;

/**
 * Objects like users and topics are not deleted. Instead, they are marked as deleted and garbage collected.
 */
public class StatusObject<E extends StatusObject> implements Matchable<E>, Updateable<E> {
    public enum Status {
        New,
        Deleted
    }

    private Status status;

    public StatusObject (Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public boolean expired() {
        return this.status == Status.Deleted;
    }

    public boolean expired(long time) {
        return expired();
    }

    public void updateFrom (StatusObject other) {
        setStatus(other.getStatus());
    }

    public boolean matches (E other) {
        return getStatus() == other.getStatus();
    }
}
