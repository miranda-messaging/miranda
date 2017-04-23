package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.SubscriptionHolder;
import com.ltsllc.miranda.subsciptions.Subscription;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/22/2017.
 */
public class GetSubscriptionsServlet extends MirandaServlet {
    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SubscriptionsResult subscriptionsResult = new SubscriptionsResult();

        try {
            List<Subscription> subscriptionList = SubscriptionHolder.getInstance().getSubscriptions();
            subscriptionsResult.setSubscriptions(subscriptionList);
            subscriptionsResult.setResult(Results.Success);
        } catch (TimeoutException e) {
            subscriptionsResult.setResult(Results.Exception);
            subscriptionsResult.setAdditionalInfo(e);
        }

        respond(resp.getOutputStream(), subscriptionsResult);
        resp.setStatus(200);
    }
}
