package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.UserHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.objects.UserListResultObject;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class GetUsersServlet extends MirandaServlet {

    public List<UserObject> toUserObjects (List<User> users) {
        List<UserObject> userObjectList = new ArrayList<UserObject>();
        for (User user : users) {
            UserObject userObject = user.asUserObject();
            userObjectList.add(userObject);
        }

        return userObjectList;
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserListResultObject userListResultObject = new UserListResultObject();

        try {
            List<User> users = UserHolder.getInstance().getUsers();
            userListResultObject.setResult(Results.Success);
            userListResultObject.setUserList(users);
        } catch (TimeoutException e) {
            userListResultObject.setResult(Results.Timeout);
        }

        respond(resp.getOutputStream(), userListResultObject);
        resp.setStatus(200);
    }
}
