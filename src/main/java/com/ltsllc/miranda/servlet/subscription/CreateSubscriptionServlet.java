package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.SubscriptionHolder;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.subsciptions.Subscription;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/22/2017.
 */
public class CreateSubscriptionServlet extends SubscriptionServlet {
    public ResultObject createResultObject() {
        return new ResultObject();
    }


    public ResultObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, SubscriptionRequestObject requestObject)
            throws ServletException, IOException, TimeoutException
    {
        ResultObject resultObject = new ResultObject();
        Results result = SubscriptionHolder.getInstance().createSubscription(requestObject.getSubscription());
        resultObject.setResult(result);

        return resultObject;
    }
}
