package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.holder.SubscriptionHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.subsciptions.Subscription;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/22/2017.
 */
public class GetSubscriptionsServlet extends SubscriptionServlet {
    public ResultObject createResultObject () {
        return new SubscriptionsResult();
    }

    public ResultObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, SubscriptionRequestObject requestObject)
            throws IOException, ServletException, TimeoutException {

        SubscriptionsResult subscriptionsResult = new SubscriptionsResult();
        List<Subscription> subscriptionList = SubscriptionHolder.getInstance().getSubscriptions();
        subscriptionsResult.setSubscriptions(subscriptionList);
        subscriptionsResult.setResult(Results.Success);

        return subscriptionsResult;
    }
}
