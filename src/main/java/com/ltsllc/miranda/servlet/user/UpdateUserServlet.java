package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.UserHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.MarshalException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class UpdateUserServlet extends MirandaServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResultObject resultObject = new ResultObject();

        try {
            UserObject userObject = fromJson(req.getInputStream(), UserObject.class);
            User user = userObject.asUser();
            Results result = UserHolder.getInstance().updateUser(user);
            resultObject.setResult(result);
        } catch (MirandaException e) {
            resultObject.setResult(Results.Exception);
            resultObject.setAdditionalInfo(e);
        } catch (TimeoutException e) {
            resultObject.setResult(Results.Timeout);
        }

        respond(resp.getOutputStream(), resultObject);
        resp.setStatus(200);
    }
}
