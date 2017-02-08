package com.ltsllc.miranda.node;

import java.util.List;

/**
 * Created by Clark on 2/6/2017.
 */
public class GetVersionsWireMessage extends WireMessage {

    public GetVersionsWireMessage() {
        super(WireSubjects.GetVersions);
    }
}
