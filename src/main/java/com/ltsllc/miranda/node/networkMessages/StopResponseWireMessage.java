package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 3/18/2017.
 */
public class StopResponseWireMessage extends WireMessage {
    public StopResponseWireMessage () {
        super(WireSubjects.StopResponse);
    }
}
