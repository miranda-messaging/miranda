package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.TopicHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.objects.UpdateTopicObject;
import com.ltsllc.miranda.topics.Topic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/9/2017.
 */
public class UpdateTopicServlet extends MirandaServlet {
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResultObject resultObject = new ResultObject();

        try {
            Topic topic = fromJson(req.getInputStream(), Topic.class);
                Results result = TopicHolder.getInstance().updateTopic(topic);
                resultObject.setResult(result);
        } catch (MirandaException e) {
            resultObject.setResult(Results.Exception);
            resultObject.setAdditionalInfo(e);
        } catch (TimeoutException e) {
            resultObject.setResult(Results.Timeout);
        }

        respond(resp.getOutputStream(), resultObject);
        resp.setStatus(200);
    }
}
