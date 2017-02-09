package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Utils;
import com.ltsllc.miranda.Version;

/**
 * Created by Clark on 2/8/2017.
 */
public class ClusterFileWireMessage extends WireMessage {
    private String content;
    private Version version;

    public String getContent() {
        return content;
    }

    public Version getVersion() {
        return version;
    }

    public ClusterFileWireMessage (byte[] file, Version version) {
        super(WireSubjects.ClusterFile);

        this.content = Utils.bytesToString(file);
        this.version = version;
    }

    public byte[] getContentAsBytes() {
        return Utils.hexStringToBytes(content);
    }
}
