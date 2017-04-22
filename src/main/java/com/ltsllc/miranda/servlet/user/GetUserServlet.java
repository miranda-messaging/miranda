package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.UserHolder;
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
public class GetUserServlet extends MirandaServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserObjectResultObject result = new UserObjectResultObject();

        try {
            UserObject userObject = fromJson(req.getInputStream(), UserObject.class);
            userObject = UserHolder.getInstance().getUser(userObject.getName()).asUserObject();
            result.setResult(Results.Success);
            result.setUserObject(userObject);
        } catch (MirandaException e) {
            result.setResult(Results.Exception);
            result.setAdditionalInfo(e);
        } catch (TimeoutException e) {
            result.setResult(Results.Timeout);
        }

        respond(resp.getOutputStream(), result);
        resp.setStatus(200);
    }
}
