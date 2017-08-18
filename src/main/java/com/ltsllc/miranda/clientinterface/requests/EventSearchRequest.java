package com.ltsllc.miranda.clientinterface.requests;

/**
 * Created by clarkhobbie on 6/22/17.
 */
public class EventSearchRequest extends Request {
    private String guidRegularExpression;

    public String getGuidRegularExpression() {
        return guidRegularExpression;
    }

    public void setGuidRegularExpression(String guidRegularExpression) {
        this.guidRegularExpression = guidRegularExpression;
    }

    public EventSearchRequest(String sessionIdString) {
        super(sessionIdString);
    }

}
