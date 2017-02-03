package com.ltsllc.miranda.file;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Topic;
import com.ltsllc.miranda.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class TopicFile  extends SingleFile<Topic> {
    public TopicFile (BlockingQueue<Message> queue, String s) {
        super(s, queue);
    }

    public Type getBasicType ()
    {
        return new TypeToken<ArrayList<User>>() {}.getType();
    }

}
