package com.ltsllc.miranda.clientinterface.requests;

import com.google.gson.Gson;

/**
 * A request sent to the Miranda system.
 *
 * <p>
 *     A request has a session ID associated with it.
 * </p>
 *
 * <p>
 *     Requests are generally sent as JSON objects to Miranda.
 * </p>
 */
public class Request {
    private static Gson gson = new Gson();

    private String sessionId;

    public Request(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getSessionIdAsLong() {
        try {
            return Long.parseLong(this.sessionId);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid session ID: " + this.sessionId);
        }
    }

    public String toJson () {
        return gson.toJson(this);
    }
}
