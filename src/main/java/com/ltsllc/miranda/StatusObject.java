package com.ltsllc.miranda;

/**
 * Created by Clark on 2/19/2017.
 */

/**
 * Objects like users and topics are not deleted. Instead, they are marked as deleted and garbage collected.
 */
public class StatusObject {
    public enum Status {
        New,
        Deleted
    }

    private Status status;

    public StatusObject (Status status) {
        this.status = status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }


    public boolean expired() {
        return this.status == Status.Deleted;
    }

}
