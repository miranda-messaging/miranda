package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * Created by Clark on 6/11/2017.
 */
public class NewEventResponseWireMessage extends WireMessage {
    public enum NewEventResponses {
        Acknowledged,
        Written,
        Error,
        Duplicate
    }

    private String guid;
    private NewEventResponses response;

    public NewEventResponseWireMessage (String guid, NewEventResponses response) {
        super(WireSubjects.NewEventResponse);

        this.guid = guid;
        this.response = response;
    }
}
