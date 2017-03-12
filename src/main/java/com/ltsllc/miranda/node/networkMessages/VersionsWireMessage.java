package com.ltsllc.miranda.node.networkMessages;

import com.ltsllc.miranda.node.NameVersion;

import java.util.List;

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
