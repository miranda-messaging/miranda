package com.ltsllc.miranda.file;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Subscription;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class SubscriptionsFile extends SingleFile<Subscription> {
    public SubscriptionsFile (BlockingQueue<Message> queue, String filename) {
        super(filename, queue);
    }

    public Type getBasicType () {
        return new TypeToken<ArrayList<Subscription>>() {}.getType();
    }
}
