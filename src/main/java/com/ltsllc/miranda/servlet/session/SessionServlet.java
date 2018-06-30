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

import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.messages.CheckSessionResponseMessage;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;


/**
 * Created by Clark on 4/28/2017.
 */
abstract public class SessionServlet extends MirandaServlet {
    abstract public Class<? extends Request> getRequestClass();

    abstract public ResultObject performService(HttpServletRequest request, HttpServletResponse response,
                                                Request requestObject) throws ServletException, IOException, TimeoutException;

    abstract public ResultObject createResultObject();

    private Session session;

    public Session getSession() {
        return session;
    }

    public Session getSession(HttpServletRequest httpServletRequest) throws TimeoutException {
        Cookie[] cookies = httpServletRequest.getCookies();
        Session session = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("sessionId")) {
                String value = cookie.getValue();
                long sessionId = Long.parseLong(value);
                session = checkSession(sessionId);
            }
        }

        if (null != session)
            setSession(session);

        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultObject resultObject = null;

        try {
            String json = Utils.readInputStream(request.getInputStream());
            Request requestObject = getGson().fromJson(json, getRequestClass());
            Session session = getSession(request);
            setSession(session);
            if (!sessionIsGood(request)) {
                resultObject = createResultObject();
                resultObject.setResult(Results.SessionNotFound);
                response.sendRedirect(LOGIN_PAGE);
            } else if (allowAccess() || session.getUser().getCategory() == User.UserTypes.Admin) {
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
        } catch (Throwable e) {
            e.printStackTrace();
            resultObject = createResultObject();
            resultObject.setResult(Results.Exception);
            resultObject.setException(e);
        }


        respond(response.getOutputStream(), resultObject);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
    }

    public boolean sessionIsGood(HttpServletRequest request) throws TimeoutException {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("sessionId")) {
                String value = cookie.getValue();
                long sessionId = Long.parseLong(value);
                Session session = checkSession(sessionId);
                return (session !=  null);
            }
        }
        return false;
    }

    public void send (BlockingQueue<Message> receiver, Message message) {
        try {
            receiver.put(message);
        } catch (InterruptedException e) {
            Panic panic = new Panic("Interrupted trying to send message", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
        }
    }

    public Session checkSession (long sessionId) throws TimeoutException {
        Miranda.getInstance().getSessionManager().sendCheckSessionMessage(getQueue(), this, sessionId);

        CheckSessionResponseMessage checkSessionResponseMessage = (CheckSessionResponseMessage) waitForReply (1000);

        return checkSessionResponseMessage.getSession();
    }
}
