package com.ltsllc.miranda.servlet.property;

import com.google.gson.Gson;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by Clark on 3/4/2017.
 */
public class PropertiesServlet extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        MirandaProperties poperties = Miranda.properties;
        Properties temp = Miranda.properties.asProperties();
        List<String> names = new ArrayList<String>(temp.stringPropertyNames());
        Collections.sort(names);

        List<Property> properties = new ArrayList<Property>();

        for (String name : names) {
            String value = temp.getProperty(name);

            properties.add(new Property(name, value));
        }

        Gson gson = new Gson();
        response.setContentType("text/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(gson.toJson(properties));
    }
}
