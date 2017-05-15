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
 * Created by Clark on 4/7/2017.
 */
public class CreateUserServlet extends UserServlet {
    public ResultObject createResultObject () {
        return new ResultObject();
    }

    public ResultObject basicService(HttpServletRequest req, HttpServletResponse resp, UserRequestObject requestObject)
            throws ServletException, IOException, TimeoutException {
        ResultObject resultObject = new ResultObject();
        UserObject user = requestObject.getUserObject();
        if (!user.isValid())
            resultObject.setResult(Results.InvalidRequest);
        else {
            User newUser = new User(user.getName(), user.getCategory(), user.getDescription(), user.getPublicKeyPem());
            Results result = UserHolder.getInstance().createUser(newUser);
            resultObject.setResult(result);
        }

        return resultObject;
    }
}
