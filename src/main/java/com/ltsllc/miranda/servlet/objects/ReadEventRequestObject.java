package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.servlet.objects.RequestObject;

/**
 * Created by clarkhobbie on 6/22/17.
 */
public class ReadEventRequestObject extends RequestObject {
    private String guid;

    public ReadEventRequestObject (String sessionIdString, String guid) {
        super(sessionIdString);

        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }
}
