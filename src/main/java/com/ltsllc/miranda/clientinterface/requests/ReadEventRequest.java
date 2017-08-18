package com.ltsllc.miranda.clientinterface.requests;

/**
 * Created by clarkhobbie on 6/22/17.
 */
public class ReadEventRequest extends Request {
    private String guid;

    public ReadEventRequest(String sessionIdString, String guid) {
        super(sessionIdString);

        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }
}
