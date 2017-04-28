package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.ServletHolder;
import com.ltsllc.miranda.servlet.holder.UserHolder;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.servlet.objects.UserObjectResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/10/2017.
 */
public class GetUserServlet extends UserServlet {
    public ResultObject createResultObject () {
        return new ResultObject();
    }

    public ResultObject basicService(HttpServletRequest request, HttpServletResponse response,
                                                UserRequestObject requestObject) throws ServletException, IOException, TimeoutException
    {
        GetUserResponseObject getUserResponseObject = new GetUserResponseObject();
        UserObject userObject = UserHolder.getInstance().getUser(requestObject.getUserObject().getName()).asUserObject();
        getUserResponseObject.setUserObject(userObject);
        getUserResponseObject.setResult(UserHolder.getInstance().getGetUserResults());

        return getUserResponseObject;
    }
}
