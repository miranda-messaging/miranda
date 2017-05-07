package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 5/3/2017.
 */
public class ShuttingDownWireMessage extends WireMessage {
    public ShuttingDownWireMessage () {
        super(WireSubjects.ShuttingDown);
    }
}
