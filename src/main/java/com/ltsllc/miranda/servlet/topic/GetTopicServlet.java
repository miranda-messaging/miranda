package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.TopicHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.objects.TopicResultObject;
import com.ltsllc.miranda.topics.Topic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class GetTopicServlet extends MirandaServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TopicResultObject topicResultObject = new TopicResultObject();
        Topic topic = null;

        try {
            topic = fromJson(req.getInputStream(), Topic.class);
            topic = TopicHolder.getInstance().getTopic(topic.getName());
            if (topic != null) {
                topicResultObject.setResult(Results.Success);
                topicResultObject.setTopic(topic);
            } else {
                topicResultObject.setResult(Results.TopicNotFound);
            }

        } catch (TimeoutException e) {
            topicResultObject.setResult(Results.Timeout);
        } catch (MirandaException e) {
            topicResultObject.setResult(Results.Exception);
            topicResultObject.setAdditionalInfo(e);
        }

        respond(resp.getOutputStream(), topicResultObject);
        resp.setStatus(200);
    }
}
