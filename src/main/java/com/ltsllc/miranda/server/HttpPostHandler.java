package com.ltsllc.miranda.server;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

import java.lang.reflect.Type;

/**
 * Created by Clark on 2/18/2017.
 */
public class HttpPostHandler extends Consumer {
    private static Gson ourGson = new Gson();

    public Object decodeContent (Type type, String json) {
        return ourGson.fromJson(json, type);
    }

    public HttpPostHandler () {
        super("http post handler");
    }
}
