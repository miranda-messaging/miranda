package com.ltsllc.miranda.node;

/**
 * Created by Clark on 2/10/2017.
 */
public class GetTopicsFileWireMessage extends WireMessage {
    public GetTopicsFileWireMessage () {
        super(WireSubjects.GetTopicsFile);
    }
}
