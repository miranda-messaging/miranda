package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.topics.Topic;

/**
 * Created by Clark on 4/15/2017.
 */
public class TopicResultObject extends ResultObject {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic (Topic topic) {
        this.topic = topic;
    }
}
