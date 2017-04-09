package com.ltsllc.miranda.servlet;

import com.google.gson.Gson;
import com.ltsllc.miranda.servlet.holder.UsersHolder;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.user.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by Clark on 4/5/2017.
 */
public class UsersServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(UsersServlet.class);
    private static Gson gson = new Gson();


    public List<UserObject> toUserObjects (List<User> userList) {
        List<UserObject> userObjects = new ArrayList<UserObject>();

        for (User user : userList) {
            UserObject userObject = user.asUserObject();
            userObjects.add(userObject);
        }

        return userObjects;
    }


    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = UsersHolder.getInstance().getUsers();
        List<UserObject> userObjects = toUserObjects(users);

        String json = gson.toJson(userObjects);
        resp.getOutputStream().println (json);
        resp.setStatus(200);
    }
}
