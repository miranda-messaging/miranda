package com.ltsllc.miranda.servlet.event;

import com.google.gson.Gson;
import com.ltsllc.commons.io.Util;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.CreateEventResultObject;
import com.ltsllc.miranda.session.Session;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 6/7/2017.
 */
public class PublishServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(PublishServlet.class);

    private Gson gson;
    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Gson getGson() {
        return gson;
    }

    public PublishServlet() {
        gson = new Gson();
    }

    public User getUser() {
        return getSession().getUser();
    }

    public void sendCreateEventResult(HttpServletResponse response, Results result) throws IOException {
        CreateEventResultObject createEventResultObject = new CreateEventResultObject();
        createEventResultObject.setResult(result);
        String json = getGson().toJson(createEventResultObject);
        response.getOutputStream().println(json);
    }

    public void sendCreateEventResult(HttpServletResponse response, EventHolder.CreateResult createResult)
            throws IOException {
        CreateEventResultObject createEventResultObject = new CreateEventResultObject();
        createEventResultObject.setResult(createResult.result);
        createEventResultObject.setGuid(createResult.guid);

        String json = getGson().toJson(createEventResultObject);
        response.getOutputStream().println(json);
    }

    public static final String HEADER_SESSION_ID = "sessionId";

    public Session checkSession(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TimeoutException, ServletException {
        String sessionIdString = null;
        for (Cookie cookie : request.getCookies())
        {
            if (cookie.getName().equalsIgnoreCase("sessionId"))
                sessionIdString = cookie.getValue();
        }


        if (null == sessionIdString) {
            logger.warn("missing session ID cookie, sessionId");
            throw new ServletException("Missing sessionId");
        }

        Long sessionId = null;

        try {
            sessionId = Long.parseLong(sessionIdString);
        } catch (NumberFormatException e) {
            logger.warn("Exception parsing session ID, " + sessionIdString, e);
            throw new ServletException("Exception parsing sessionId");
        }

        Session session = EventHolder.getInstance().getSession(sessionId);

        if (null == session) {
            logger.warn("Session not found, " + sessionId);
            sendCreateEventResult(response, Results.SessionNotFound);
        }

        setSession(session);
        return session;
    }

    public String getTopicName(HttpServletRequest request, HttpServletResponse response)
            throws IOException, MirandaException {
        char[] url = request.getRequestURL().toString().toCharArray();

        int index = url.length - 1;
        while (index >= 0 && url[index] != '/')
            index--;

        if (index < 0) {
            sendCreateEventResult(response, Results.MissingTopic);
            logger.warn("Missing topic name in " + request.getRequestURL());
            throw new MirandaException("missing topic name");
        }

        index++;
        if (index >= url.length) {
            sendCreateEventResult(response, Results.MissingTopic);
            logger.warn("Missing topic name in " + request.getRequestURL());
            throw new MirandaException("missing topic name");
        }

        int count = url.length - index;
        return new String(url, index, count);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Session session = checkSession(request, response);
            String topicName = getTopicName(request, response);
            byte[] content = Util.readCompletely(request.getInputStream());
            Event event = new Event(getUser(), Event.Methods.POST, topicName, content);
            EventHolder.CreateResult createResult = EventHolder.getInstance().create(event, session);
            sendCreateEventResult(response, createResult);
            response.setStatus(200);
        } catch (TimeoutException e) {
            e.printStackTrace();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream());
            outputStreamWriter.write("Timeout");
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(400);
        }
    }

}
