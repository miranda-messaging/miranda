package com.ltsllc.miranda.node.networkMessages;

import com.google.gson.Gson;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.Version;

import java.io.IOException;
import java.util.List;

/**
 * Created by Clark on 2/8/2017.
 */
public class ClusterFileWireMessage extends WireMessage {
    private static Gson ourGson = new Gson();

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

    public ClusterFileWireMessage (List<NodeElement> file, Version version) {
        super(WireSubjects.ClusterFile);

        String json = ourGson.toJson(file);
        byte[] buffer = json.getBytes();

        this.content = Utils.bytesToString(buffer);
        this.version = version;
    }

    public byte[] getContentAsBytes() throws IOException {
        return Utils.hexStringToBytes(content);
    }
}
