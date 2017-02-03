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
        Join,
        JoinSuccess
    }

    private WireSubjects subject;

    public WireSubjects getSubeject() {
        return subject;
    }

    public WireMessage (WireSubjects subject) {
        this.subject = subject;
    }

    public String getJson () {
        return ourGson.toJson(this);
    }
}
