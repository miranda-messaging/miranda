package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class VersionsWireMessage extends WireMessage {
    private List<NameVersion> versions;

    public List<NameVersion> getVersions() {
        return versions;
    }

    public VersionsWireMessage (List<NameVersion> versions) {
        super(WireSubjects.Versions);
        this.versions = versions;
    }
}
