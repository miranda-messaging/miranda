package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 3/18/2017.
 */
public class StoppingWireMessage extends WireMessage {
    public StoppingWireMessage () {
        super(WireSubjects.Stopping);
    }
}
