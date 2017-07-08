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

package com.ltsllc.miranda.servlet.session;

import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.session.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * Created by Clark on 4/28/2017.
 */
abstract public class SessionServlet extends MirandaServlet {
    abstract public Class<? extends Request> getRequestClass();

    abstract public ResultObject performService(HttpServletRequest request, HttpServletResponse response,
                                                Request requestObject) throws ServletException, IOException, TimeoutException;

    abstract public ServletHolder getServletHolder();

    abstract public ResultObject createResultObject();
    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser () {
        return getSession().getUser();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultObject resultObject = null;

        try {
            String json = Utils.readInputStream(request.getInputStream());
            Request requestObject = getGson().fromJson(json, getRequestClass());

            setSession(getServletHolder().getSession(requestObject.getSessionIdAsLong()));
            if (null == getSession()) {
                resultObject = createResultObject();
                resultObject.setResult(Results.SessionNotFound);
                response.sendRedirect(LOGIN_PAGE);
            } else if (allowAccess() || getSession().getUser().getCategory() == User.UserTypes.Admin) {
                resultObject = performService(request, response, requestObject);
            } else {
                resultObject = createResultObject();
                resultObject.setResult(Results.InsufficientPermissions);
            }
        } catch (TimeoutException e) {
            resultObject = createResultObject();
            resultObject.setResult(Results.Timeout);
        } catch (IOException | ServletException e) {
            resultObject = createResultObject();
            resultObject.setResult(Results.Exception);
            resultObject.setException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }


        respond(response.getOutputStream(), resultObject);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
    }


}
