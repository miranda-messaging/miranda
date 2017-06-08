package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.session.SessionServlet;
import com.ltsllc.miranda.servlet.subscription.SubscriptionHolder;
import com.ltsllc.miranda.servlet.subscription.SubscriptionRequestObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.ltsllc.miranda.user.User.UserTypes.Subscriber;

/**
 * Created by Clark on 6/7/2017.
 */
public abstract class CreateEventServlet extends SessionServlet{
        abstract ResultObject basicPerformService(HttpServletRequest request, HttpServletResponse response,
                                                  SubscriptionRequestObject requestObject)
                throws IOException, ServletException, TimeoutException;

        public ServletHolder getServletHolder() {
            return SubscriptionHolder.getInstance();
        }

        public Class getRequestClass() {
            return SubscriptionRequestObject.class;
        }

        public boolean allowAccess() {
            return getSession().getUser().getCategory() == Subscriber;
        }

        public ResultObject performService(HttpServletRequest request, HttpServletResponse response, RequestObject requestObject)
                throws IOException, ServletException, TimeoutException {
            SubscriptionRequestObject subscriptionRequestObject = (SubscriptionRequestObject) requestObject;
            ResultObject resultObject = basicPerformService(request, response, subscriptionRequestObject);
            return resultObject;
        }

}
