package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.topics.Topic;

/**
 * Created by Clark on 4/28/2017.
 */
public class TopicRequestObject extends RequestObject {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
