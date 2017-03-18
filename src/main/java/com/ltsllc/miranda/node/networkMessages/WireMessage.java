package com.ltsllc.miranda.node.networkMessages;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class WireMessage {
    private static Gson ourGson = new Gson();

    public enum WireSubjects {
        ClusterFile,
        GetClusterFile,
        GetFile,
        GetFileResponse,
        GetSubscriptionsFile,
        GetTopicsFile,
        GetUsersFile,
        GetVersions,
        Join,
        JoinResponse,
        Versions,
        Version
    }

    private WireSubjects subject;
    private String className;

    public WireSubjects getWireSubject() {
        return subject;
    }

    public String getClassName() {
        return className;
    }

    public WireMessage (WireSubjects subject) {
        this.subject = subject;
        this.className = getClass().getCanonicalName();
    }

    public String getJson () {
        return ourGson.toJson(this);
    }

    public boolean equals (Object o) {
        if (o == null || !(o instanceof WireMessage))
            return false;

        WireMessage other = (WireMessage) o;
        return getWireSubject().equals(other.getWireSubject())
                && getClassName().equals(other.getClassName());
    }
}
