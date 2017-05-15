package com.ltsllc.miranda.servlet.session;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/28/2017.
 */
abstract public class SessionServlet extends MirandaServlet {
    abstract public Class<? extends RequestObject> getRequestClass();

    abstract public ResultObject performService(HttpServletRequest request, HttpServletResponse response,
                                                RequestObject requestObject) throws ServletException, IOException, TimeoutException;

    abstract public ServletHolder getServletHolder();

    abstract public ResultObject createResultObject();
    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultObject resultObject = null;

        try {
            RequestObject requestObject = fromJson(request.getInputStream(), getRequestClass());
            setSession(getServletHolder().getSession(requestObject.getSessionId()));
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
        } catch (MirandaException | IOException | ServletException e) {
            resultObject = createResultObject();
            resultObject.setResult(Results.Exception);
            resultObject.setAdditionalInfo(e);
        }

        respond(response.getOutputStream(), resultObject);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
    }


}