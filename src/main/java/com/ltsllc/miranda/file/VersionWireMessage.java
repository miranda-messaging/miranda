package com.ltsllc.miranda.file;

import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * Created by Clark on 2/11/2017.
 */
public class VersionWireMessage extends WireMessage {
    private NameVersion nameVersion;

    public VersionWireMessage (NameVersion nameVersion) {
        super(WireSubjects.Version);

        this.nameVersion = nameVersion;
    }

    public NameVersion getNameVersion() {
        return nameVersion;
    }
}
