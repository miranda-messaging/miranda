package com.ltsllc.miranda.servlet;

import com.google.gson.Gson;
import com.ltsllc.miranda.servlet.objects.MirandaStatus;
import com.ltsllc.miranda.servlet.objects.StatusObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Clark on 3/4/2017.
 */
public class StatusServlet extends HttpServlet {
    private static Gson ourGson = new Gson();

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        MirandaStatus mirandaStatus = MirandaStatus.getInstance();
        StatusObject statusObject = mirandaStatus.getStatus();

        response.getOutputStream().print(ourGson.toJson(statusObject));
    }
}
