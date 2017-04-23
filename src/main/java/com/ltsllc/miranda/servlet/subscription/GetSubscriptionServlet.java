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
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/22/2017.
 */
public class GetSubscriptionServlet extends MirandaServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SubscriptionResultObject resultObject = new SubscriptionResultObject();

        try {
            Subscription subscription = fromJson(req.getInputStream(), Subscription.class);
            subscription = SubscriptionHolder.getInstance().getSubscription(subscription.getName());
            if (null == subscription)
                resultObject.setResult(Results.SubscriptionNotFound);
            else {
                resultObject.setSubscription(subscription);
                resultObject.setResult(Results.Success);
            }
        } catch (IOException | MirandaException | TimeoutException e) {
            resultObject.setResult(Results.Exception);
            resultObject.setAdditionalInfo(e);
        }

        respond(resp.getOutputStream(), resultObject);
        resp.setStatus(200);
    }
}
