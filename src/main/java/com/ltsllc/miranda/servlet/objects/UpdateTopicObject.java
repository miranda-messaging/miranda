package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.topics.Topic;

/**
 * Created by Clark on 4/9/2017.
 */
public class UpdateTopicObject {
    private Topic oldTopic;
    private Topic newTopic;

    public Topic getOldTopic() {
        return oldTopic;
    }

    public Topic getNewTopic() {
        return newTopic;
    }
}
