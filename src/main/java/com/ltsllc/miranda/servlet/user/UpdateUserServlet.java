package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class UpdateUserServlet extends UserServlet {
    public ResultObject createResultObject() {
        return new ResultObject();
    }

    public boolean allowAccess () {
        return true;
    }

    public ResultObject basicService(HttpServletRequest req, HttpServletResponse resp, UserRequestObject requestObject)
            throws ServletException, IOException, TimeoutException {
        ResultObject resultObject = new ResultObject();
        User user = requestObject.getUserObject().asUser();
        Results result = UserHolder.getInstance().updateUser(user);
        resultObject.setResult(result);

        return resultObject;
    }
}
