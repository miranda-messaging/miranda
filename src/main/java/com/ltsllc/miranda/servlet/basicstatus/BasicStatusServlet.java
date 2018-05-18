package com.ltsllc.miranda.servlet.basicstatus;

import com.ltsllc.miranda.servlet.miranda.MirandaServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BasicStatusServlet extends MirandaServlet {
    public void doGet (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.getOutputStream().println("OK");
    }
}
