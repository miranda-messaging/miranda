package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.SessionServlet;
import com.ltsllc.miranda.servlet.holder.ServletHolder;
import com.ltsllc.miranda.servlet.holder.UserHolder;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.user.User;

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
                                               UserRequestObject requestObject) throws ServletException, IOException, TimeoutException;

    public ServletHolder getServletHolder() {
        return UserHolder.getInstance();
    }

    public Class getRequestClass () {
        return UserRequestObject.class;
    }

    public boolean allowAccess () {
        return false;
    }

    public ResultObject performService(HttpServletRequest request, HttpServletResponse response,
                                                RequestObject requestObject) throws ServletException, IOException, TimeoutException
    {
        UserRequestObject userRequestObject = (UserRequestObject) requestObject;

        if (getSession().getUser().getCategory() != User.UserTypes.Admin) {
            ResultObject resultObject = createResultObject();
            resultObject.setResult(Results.InsufficientPermissions);
            return resultObject;
        } else {
            return basicService(request, response, userRequestObject);
        }
    }

}
