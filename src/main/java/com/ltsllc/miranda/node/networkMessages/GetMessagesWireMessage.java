package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 3/22/2017.
 */
public class GetMessagesWireMessage extends WireMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public GetMessagesWireMessage (String filename) {
        super (WireSubjects.GetMessages);

        this.filename = filename;
    }
}
