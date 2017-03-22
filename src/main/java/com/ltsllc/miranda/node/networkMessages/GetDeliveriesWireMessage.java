package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 3/22/2017.
 */
public class GetDeliveriesWireMessage extends WireMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public GetDeliveriesWireMessage (String filename) {
        super(WireSubjects.GetDeliveries);

        this.filename = filename;
    }
}
