package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by Clark on 3/13/2017.
 */
public class SetPropertyServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(SetPropertyServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, String> newValues = new HashMap<String, String>();

        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String newValue = request.getParameter(name);
            String oldValue = Miranda.properties.getProperty(name);

            if (!newValue.equals(oldValue)) {
                newValues.put(name, newValue);
            }
        }

        logger.info("new values:");
        for (String name : newValues.keySet())
        {
            String value = newValues.get(name);
            logger.info (name + " = " + value);
        }

        if (newValues.size() > 0) {
            for (String name : newValues.keySet()) {
                String value = newValues.get(name);
                Miranda.properties.setProperty(name, value);
            }

            Miranda.properties.write();
        }
    }

}
