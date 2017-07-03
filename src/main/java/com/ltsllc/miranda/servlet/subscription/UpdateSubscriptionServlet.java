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

import com.ltsllc.miranda.clientinterface.Results;
import com.ltsllc.miranda.clientinterface.requests.SubscriptionRequest;
import com.ltsllc.miranda.clientinterface.results.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/22/2017.
 */
public class UpdateSubscriptionServlet extends SubscriptionServlet {
    public ResultObject createResultObject() {
        return new ResultObject();
    }

    public ResultObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, SubscriptionRequest requestObject)
            throws ServletException, IOException, TimeoutException {
        ResultObject resultObject = new ResultObject();
        Results result = SubscriptionHolder.getInstance().updateSubscription(requestObject.getSubscription());
        resultObject.setResult(result);

        return resultObject;
    }
}
