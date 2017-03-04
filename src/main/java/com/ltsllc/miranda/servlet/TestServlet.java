package com.ltsllc.miranda.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Clark on 3/4/2017.
 */
public class TestServlet extends HttpServlet {
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
            IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello from HelloServlet</h1>");
    }
}
