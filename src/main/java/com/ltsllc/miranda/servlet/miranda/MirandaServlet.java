package com.ltsllc.miranda.servlet.miranda;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ltsllc.miranda.MirandaException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by Clark on 4/7/2017.
 */
public class MirandaServlet extends HttpServlet {
    public void doOptions (HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Allow", "*");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, Access-Control-Allow-Origin");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.setHeader("Access-Control-Max-Age", "1209600");
    }


    public boolean allowAccess () {
        return true;
    }

    public static final String LOGIN_PAGE = "/login.html";

    private Gson gson = new Gson();

    public String read(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        StringWriter stringWriter = new StringWriter();
        for (int c = inputStreamReader.read(); c != -1; c = inputStreamReader.read()) {
            stringWriter.append((char) c);
        }

        return stringWriter.toString();
    }

    public <T> T fromJson(InputStream inputStream, Class<T> type) throws MirandaException {
        try {
            String json = read(inputStream);
            return gson.fromJson(json, type);
        } catch (IOException | JsonSyntaxException e) {
            throw new MirandaException("Exception trying to get object", e);
        }
    }

    public void respond(ServletOutputStream output, Object o) throws IOException {
        String json = gson.toJson(o);
        output.println(json);
    }
}
