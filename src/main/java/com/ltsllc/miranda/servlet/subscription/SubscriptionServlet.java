/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.requests.SubscriptionRequest;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.session.SessionServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.ltsllc.miranda.clientinterface.basicclasses.User.UserTypes.Subscriber;

/**
 * Created by Clark on 4/28/2017.
 */
abstract public class SubscriptionServlet extends SessionServlet {
    abstract ResultObject basicPerformService(HttpServletRequest request, HttpServletResponse response,
                                              SubscriptionRequest requestObject)
            throws IOException, ServletException, TimeoutException;

    public ServletHolder getServletHolder() {
        return SubscriptionHolder.getInstance();
    }

    public Class getRequestClass() {
        return SubscriptionRequest.class;
    }

    public boolean allowAccess() {
        return getSession().getUser().getCategory() == Subscriber;
    }

    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, Request requestObject)
            throws IOException, ServletException, TimeoutException {
        SubscriptionRequest subscriptionRequestObject = (SubscriptionRequest) requestObject;
        ResultObject resultObject = basicPerformService(request, response, subscriptionRequestObject);
        return resultObject;
    }
}
