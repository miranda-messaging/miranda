package com.ltsllc.miranda.node;

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
        Join,
        JoinSuccess,
        GetVersions,
        Versions
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
}
