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

package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.requests.UserRequest;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.session.SessionServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/27/2017.
 */
abstract public class UserServlet extends SessionServlet {
    abstract public ResultObject basicService (HttpServletRequest request, HttpServletResponse response,
                                               UserRequest requestObject) throws ServletException, IOException, TimeoutException;

    public ServletHolder getServletHolder() {
        return UserHolder.getInstance();
    }

    public Class getRequestClass () {
        return UserRequest.class;
    }

    public boolean allowAccess () {
        return false;
    }

    public ResultObject performService(HttpServletRequest request, HttpServletResponse response,
                                                Request requestObject) throws ServletException, IOException, TimeoutException
    {
        UserRequest userRequestObject = (UserRequest) requestObject;

        if (getSession().getUser().getCategory() != User.UserTypes.Admin) {
            ResultObject resultObject = createResultObject();
            resultObject.setResult(Results.InsufficientPermissions);
            return resultObject;
        } else {
            return basicService(request, response, userRequestObject);
        }
    }

}
