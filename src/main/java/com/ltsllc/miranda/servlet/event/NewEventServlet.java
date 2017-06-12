package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler for new events
 *
 * When an HTTP POST/PUT or DELETE to a topic occurs, this servlet is invoked.  It looks for
 * a header, {@link #HEADER_SESSION_ID} that contains the session ID for the user.  The user
 * must be the owner of the topic or an admin to create new events.
 *
 * The topic name is obtained from the URL
 */
public class NewEventServlet extends HttpServlet {
    public static final String HEADER_SESSION_ID = "sessionId";

    private static String baseUrl;

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        NewEventServlet.baseUrl = baseUrl;
    }

    /**
     * Return the topic this event pertains to,
     * @param url The URL the event is being published to.
     * @param base The base URL used to publish the even.
     * @return The name of the topic the event is being published to.
     */
    public String getTopicName (String url, String base) {
        if (!(url.startsWith(base))) {
            Panic panic = new Panic("URL, " + url + ", does not start with base URL, " + base,
                    Panic.Reasons.BadURL);
            Miranda.panicMiranda(panic);

            throw new RuntimeException("Bad URL, " + url);
        }

        return url.substring(base.length());
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

}
