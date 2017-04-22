package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.TopicHolder;
import com.ltsllc.miranda.servlet.objects.TopicsResultObject;
import com.ltsllc.miranda.topics.Topic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicsServlet extends MirandaServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TopicsResultObject result = new TopicsResultObject();

        try {
            List<Topic> topics = TopicHolder.getInstance().getTopicList();
            result.setResult(Results.Success);
            result.setTopicList(topics);
        } catch (TimeoutException e) {
            result.setResult(Results.Timeout);
        }

        respond(resp.getOutputStream(), result);
        resp.setStatus(200);
    }
}
