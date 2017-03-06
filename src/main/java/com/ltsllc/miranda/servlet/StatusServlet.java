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
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        /*
        List<NodeElement> nodes = new ArrayList<NodeElement>();


        Cluster cluster = Miranda.getInstance().getCluster();
        for (Node node : cluster.getNodes()) {
            nodes.add(node.asNodeElement());
        }

        MirandaProperties properties = Miranda.properties;
        List<String> names = new ArrayList<String>(properties.asProperties().stringPropertyNames());
        Collections.sort(names);
        List<Property> sortedProperties = new ArrayList<Property>();

        for (String name : names) {
            String value = properties.getProperty(name);
            sortedProperties.add(new Property(name, value));
        }

        NodeElement local = new NodeElement(
                properties.getProperty(MirandaProperties.PROPERTY_MY_DNS),
                properties.getProperty(MirandaProperties.PROPERTY_MY_IP),
                properties.getIntProperty(MirandaProperties.PROPERTY_MY_PORT),
                properties.getProperty(MirandaProperties.PROPERTY_MY_DESCIPTION)
        );

        StatusObject status = new StatusObject(local, sortedProperties, nodes);

        Gson gson = new Gson();
        response.setContentType("text/jason");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(gson.toJson(status));
*/
    }
}
