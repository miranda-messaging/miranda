package com.ltsllc.miranda.node;

/**
 * Created by Clark on 2/10/2017.
 */
public class GetUsersFileWireMessage extends WireMessage {
    public GetUsersFileWireMessage () {
        super(WireSubjects.GetUsersFile);
    }
}
