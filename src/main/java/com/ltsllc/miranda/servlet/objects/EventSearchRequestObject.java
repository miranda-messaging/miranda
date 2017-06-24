package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.servlet.objects.RequestObject;

/**
 * Created by clarkhobbie on 6/22/17.
 */
public class EventSearchRequestObject extends RequestObject {
    private String guidRegularExpression;

    public String getGuidRegularExpression() {
        return guidRegularExpression;
    }

    public void setGuidRegularExpression(String guidRegularExpression) {
        this.guidRegularExpression = guidRegularExpression;
    }

    public EventSearchRequestObject(String sessionIdString) {
        super(sessionIdString);
    }

}
