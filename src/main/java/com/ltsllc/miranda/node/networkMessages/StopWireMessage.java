package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 3/18/2017.
 */
public class StopWireMessage extends WireMessage {
    public StopWireMessage () {
        super(WireSubjects.Stop);
    }
}
