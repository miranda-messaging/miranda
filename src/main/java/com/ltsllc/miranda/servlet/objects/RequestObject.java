package com.ltsllc.miranda.servlet.objects;

/**
 * Created by Clark on 4/27/2017.
 */
public class RequestObject {
    private String sessionIdString;

    public String getSessionIdString() {
        return sessionIdString;
    }

    public void setSessionIdString(String sessionIdString) {
        this.sessionIdString = sessionIdString;
    }

    public long getSessionId () {
        return Long.parseLong(getSessionIdString());
    }
}
