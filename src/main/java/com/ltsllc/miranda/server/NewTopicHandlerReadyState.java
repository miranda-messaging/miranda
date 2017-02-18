package com.ltsllc.miranda.server;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Topic;
import com.ltsllc.miranda.topics.TopicsFile;

import java.lang.reflect.Type;

/**
 * Created by Clark on 2/18/2017.
 */
public class NewTopicHandlerReadyState extends NewObjectHandlerReadyState<TopicsFile, Topic, NewTopicHandler> {
    public Type getBasicType() {
        return Topic.class;
    }

    public NewTopicHandlerReadyState (Consumer consumer, TopicsFile file, NewTopicHandler handler) {
        super(consumer, file, handler);
    }

}
