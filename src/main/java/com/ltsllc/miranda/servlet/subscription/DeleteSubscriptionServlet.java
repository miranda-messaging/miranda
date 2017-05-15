package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.holder.SubscriptionHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionServlet extends SubscriptionServlet {
    public ResultObject createResultObject () {
        return new ResultObject();
    }

    public ResultObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, SubscriptionRequestObject requestObject)
            throws ServletException, IOException, TimeoutException {
        ResultObject resultObject = new ResultObject();
        Results result = SubscriptionHolder.getInstance().deleteSubscription(requestObject.getSubscription().getName());
        resultObject.setResult(result);

        return resultObject;
    }
}
