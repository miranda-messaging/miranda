package com.ltsllc.miranda.servlet;

import com.google.gson.Gson;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.property.MirandaProperties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
