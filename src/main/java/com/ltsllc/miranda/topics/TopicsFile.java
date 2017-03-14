package com.ltsllc.miranda.topics;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Topic;
import com.ltsllc.miranda.User;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.writer.Writer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class TopicsFile extends SingleFile<Topic> {
    private static TopicsFile ourInstance;

    public static TopicsFile getInstance() {
        return ourInstance;
    }

    public static synchronized void initialize (String filename, Writer writer) {
        if (null == ourInstance) {
            ourInstance = new TopicsFile(filename, writer);
            ourInstance.start();
            ourInstance.load();
        }
    }

    private TopicsFile(String filename, Writer writer) {
        super(filename, writer);
        TopicsFileReadyState topicsFileReadyState = new TopicsFileReadyState(this);
        setCurrentState(topicsFileReadyState);
    }

    public Type getBasicType ()
    {
        return new TypeToken<ArrayList<User>>() {}.getType();
    }

    public List buildEmptyList () {
        return new ArrayList<Topic> ();
    }

    public Type listType () {
        return new TypeToken<ArrayList<Topic>>(){}.getType();
    }


}
