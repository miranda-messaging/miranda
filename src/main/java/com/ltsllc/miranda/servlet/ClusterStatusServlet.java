package com.ltsllc.miranda.servlet;

import com.google.gson.Gson;
import com.ltsllc.miranda.servlet.holder.ClusterStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Clark on 3/9/2017.
 */
public class ClusterStatusServlet extends HttpServlet {
    private static Gson ourGson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ClusterStatus clusterStatus = ClusterStatus.getInstance();
        ClusterStatusObject clusterStatusObject = clusterStatus.getClusterStatus();
        String json = ourGson.toJson(clusterStatusObject);
        resp.setContentType("text/json");
        resp.getOutputStream().print(json);
    }
}
