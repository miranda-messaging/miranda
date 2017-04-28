package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.SessionServlet;
import com.ltsllc.miranda.servlet.holder.ServletHolder;
import com.ltsllc.miranda.servlet.holder.SubscriptionHolder;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.ltsllc.miranda.user.User.UserTypes.Subscriber;

/**
 * Created by Clark on 4/28/2017.
 */
abstract public class SubscriptionServlet extends SessionServlet {
    abstract ResultObject basicPerformService (HttpServletRequest request, HttpServletResponse response,
                                               SubscriptionRequestObject requestObject)
        throws IOException, ServletException, TimeoutException;

    public ServletHolder getServletHolder () {
        return SubscriptionHolder.getInstance();
    }

    public Class getRequestClass () {
        return SubscriptionRequestObject.class;
    }

    public boolean allowAccess () {
        return getSession().getUser().getCategory() == Subscriber;
    }

    public ResultObject performService (HttpServletRequest request, HttpServletResponse response, RequestObject requestObject)
        throws IOException, ServletException, TimeoutException
    {
        SubscriptionRequestObject subscriptionRequestObject = (SubscriptionRequestObject) requestObject;
        ResultObject resultObject = basicPerformService(request, response, subscriptionRequestObject);
        return resultObject;
    }
}
