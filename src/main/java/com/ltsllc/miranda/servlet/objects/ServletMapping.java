package com.ltsllc.miranda.servlet.objects;

import javax.servlet.http.HttpServlet;

/**
 * Created by Clark on 3/9/2017.
 */
public class ServletMapping {
    private String path;
    private Class servletClass;

    public String getPath() {
        return path;
    }

    public Class getServletClass() {
        return servletClass;
    }

    public ServletMapping (String path, Class servletClass) {
        this.path = path;
        this.servletClass = servletClass;
    }
}
