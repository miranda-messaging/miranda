package com.ltsllc.miranda.topics;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.topics.states.TopicsFileReadyState;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 1/5/2017.
 */
public class TopicsFile extends SingleFile<Topic> {
    public static final String FILE_NAME = "topics";

    private static Logger logger = Logger.getLogger(TopicsFile.class);

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

    public static void setInstance (TopicsFile topicsFile) {
        ourInstance = topicsFile;
    }

    public TopicsFile(String filename, Writer writer) {
        super(filename, writer);
        TopicsFileReadyState topicsFileReadyState = new TopicsFileReadyState(this);
        setCurrentState(topicsFileReadyState);
    }

    public TopicsFile () {
        super(null, null);
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

    public void checkForDuplicates () {
        List<Topic> topicList = new ArrayList<Topic>(getData());
        List<Topic> duplicates = new ArrayList<Topic>();

        for (Topic current : topicList) {
            for (Topic topic : getData())
            {
                if (current.getName().equals(topic.getName()) && current != topic)
                {
                    logger.info ("Removing duplicate topic named " + topic.getName());
                    duplicates.add(current);
                }
            }
        }

        getData().removeAll(duplicates);
    }

    public Topic find (Topic topic) {
        for (Topic candidate : getData()) {
            if (candidate.getName().equals(topic.getName()))
                return candidate;
        }

        return null;
    }
}
