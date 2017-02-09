package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */
public class GetClusterFileWireMessage extends WireMessage {
    public GetClusterFileWireMessage () {
        super(WireSubjects.GetClusterFile);
    }
}
