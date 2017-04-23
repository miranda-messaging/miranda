package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.SubscriptionHolder;
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
public class DeleteSubscriptionServlet extends MirandaServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResultObject resultObject = new ResultObject();

        try {
            Subscription subscription = fromJson(req.getInputStream(), Subscription.class);
            Results result = SubscriptionHolder.getInstance().deleteSubscription(subscription.getName());
            resultObject.setResult(result);
        } catch (MirandaException | TimeoutException e) {
            resultObject.setResult(Results.Exception);
            resultObject.setAdditionalInfo(e);
        }

        respond(resp.getOutputStream(), resultObject);
        resp.setStatus(200);
    }
}
