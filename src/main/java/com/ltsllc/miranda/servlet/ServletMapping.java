package com.ltsllc.miranda.servlet;

import javax.servlet.http.HttpServlet;

/**
 * Created by Clark on 3/9/2017.
 */
public class ServletMapping {
    private String path;
    private Class servlet;

    public String getPath() {
        return path;
    }

    public Class getServlet() {
        return servlet;
    }

    public ServletMapping (String path, Class servlet) {
        this.path = path;
        this.servlet = servlet;
    }
}
