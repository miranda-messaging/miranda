package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.holder.TopicHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.topics.Topic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class GetTopicServlet extends TopicServlet {
    public ResultObject createResultObject () {
        return new TopicResultObject();
    }

    public ResultObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, TopicRequestObject requestObject)
            throws ServletException, IOException, TimeoutException {
        TopicResultObject topicResultObject = new TopicResultObject();
        Topic topic = TopicHolder.getInstance().getTopic(requestObject.getTopic().getName());
        if (topic != null) {
            topicResultObject.setResult(Results.Success);
            topicResultObject.setTopic(topic);
        } else {
            topicResultObject.setResult(Results.TopicNotFound);
        }

        return topicResultObject;
    }
}
