package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.topics.Topic;

import java.util.List;

/**
 * Created by Clark on 4/15/2017.
 */
public class TopicsResultObject extends ResultObject {
    private List<Topic> topicList;

    public List<Topic> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;
    }
}
