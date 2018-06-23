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

package com.ltsllc.miranda.servlet.misc;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.session.SessionServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 5/1/2017.
 */
public class ShutdownServlet extends SessionServlet {
    public Class getRequestClass() {
        return Request.class;
    }

    public ResultObject createResultObject() {
        return new ResultObject();
    }

    public boolean allowAccess() {
        return getSession().getUser().getCategory() == User.UserTypes.Admin;
    }

    public ServletHolder getServletHolder() {
        return ShutdownHolder.getInstance();
    }

    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, Request requestObject)
            throws TimeoutException {
        ShutdownHolder.getInstance().shutdownMirada();

        ResultObject resultObject = new ResultObject();
        resultObject.setResult(Results.Success);
        return resultObject;
    }
}
