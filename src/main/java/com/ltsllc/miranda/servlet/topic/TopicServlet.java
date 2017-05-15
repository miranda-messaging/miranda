package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.servlet.session.SessionServlet;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/28/2017.
 */
abstract public class TopicServlet extends SessionServlet {
    abstract ResultObject basicPerformService (HttpServletRequest request, HttpServletResponse response,
                                               TopicRequestObject requestObject) throws IOException, ServletException, TimeoutException;

    public boolean allowAccess () {
        return true;
    }

    public Class getRequestClass () {
        return TopicRequestObject.class;
    }

    public ResultObject performService (HttpServletRequest request, HttpServletResponse response, RequestObject requestObject)
            throws IOException, ServletException, TimeoutException
    {
        TopicRequestObject topicRequestObject = (TopicRequestObject) requestObject;
        ResultObject resultObject = basicPerformService(request, response, topicRequestObject);
        return resultObject;
    }

    public ServletHolder getServletHolder () {
        return TopicHolder.getInstance();
    }
}
