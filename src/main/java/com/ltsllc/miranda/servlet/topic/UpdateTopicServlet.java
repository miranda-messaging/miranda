package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.holder.TopicHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/9/2017.
 */
public class UpdateTopicServlet extends TopicServlet {
    public ResultObject createResultObject () {
        return new ResultObject();
    }

    public ResultObject basicPerformService(HttpServletRequest request, HttpServletResponse response,
                                       TopicRequestObject requestObject) throws ServletException, IOException, TimeoutException {
        ResultObject resultObject = new ResultObject();

        Results result = TopicHolder.getInstance().updateTopic(requestObject.getTopic());
        resultObject.setResult(result);

        return resultObject;
    }
}
