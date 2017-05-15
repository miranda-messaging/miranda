package com.ltsllc.miranda.servlet.misc;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.session.SessionServlet;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 5/1/2017.
 */
public class ShutdownServlet extends SessionServlet {
    public Class getRequestClass () {
        return RequestObject.class;
    }

    public ResultObject createResultObject () {
        return new ResultObject();
    }

    public boolean allowAccess () {
        return getSession().getUser().getCategory() == User.UserTypes.Admin;
    }

    public com.ltsllc.miranda.servlet.holder.ServletHolder getServletHolder () {
        return ShutdownHolder.getInstance();
    }

    public ResultObject performService (HttpServletRequest request, HttpServletResponse response, RequestObject requestObject)
            throws TimeoutException
    {
        ShutdownHolder.getInstance().shutdownMirada();

        ResultObject resultObject = new ResultObject();
        resultObject.setResult(Results.Success);
        return resultObject;
    }
}
