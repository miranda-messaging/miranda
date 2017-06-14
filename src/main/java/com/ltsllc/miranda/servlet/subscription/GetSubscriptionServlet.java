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

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.objects.ReadObject;
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
public class GetSubscriptionServlet extends SubscriptionServlet {
    public ResultObject createResultObject() {
        return new ResultObject();
    }

    public ReadObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, SubscriptionRequestObject requestObject)
            throws ServletException, IOException, TimeoutException {
        ReadObject<Subscription> resultObject = new ReadObject();

        Subscription subscription = SubscriptionHolder.getInstance().getSubscription(requestObject.getSubscription().getName());
        if (null == subscription)
            resultObject.setResult(Results.SubscriptionNotFound);
        else {
            resultObject.setObject(subscription);
            resultObject.setResult(Results.Success);
        }

        return resultObject;
    }
}
