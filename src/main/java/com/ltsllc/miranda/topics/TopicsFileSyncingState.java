package com.ltsllc.miranda.topics;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Topic;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.SingleFileSyncingState;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/12/2017.
 */
public class TopicsFileSyncingState extends SingleFileSyncingState {
    private TopicsFile topicsFile;

    public TopicsFileSyncingState (TopicsFile topicsFile) {
        super(topicsFile);

        this.topicsFile = topicsFile;
    }

    public TopicsFile getTopicsFile() {
        return topicsFile;
    }

    @Override
    public Type getListType() {
        return new TypeToken<List<Topic>>(){}.getType();
    }

    @Override
    public boolean contains(Object o) {
        Topic topic = (Topic) o;

        for (Topic t : getTopicsFile().getData()) {
            if (t.equals(topic))
                return true;
        }

        return false;
    }

    @Override
    public State getReadyState() {
        TopicsFileReadyState topicsFileReadyState = new TopicsFileReadyState(getTopicsFile());
        return topicsFileReadyState;
    }


    @Override
    public List getData() {
        return getTopicsFile().getData();
    }


    @Override
    public String getName() {
        return "topics";
    }


    @Override
    public SingleFile getFile() {
        return getTopicsFile();
    }
}
