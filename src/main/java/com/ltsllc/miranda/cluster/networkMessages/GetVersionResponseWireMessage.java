package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GetVersionResponseWireMessage extends WireMessage {
    private Map<Files, Version> fileToVersion = new HashMap<Files, Version>();
    private Node node;
    private boolean error = false;

    public GetVersionResponseWireMessage() {
        super(WireSubjects.GetVersionsResponse);
    }

    public Version getVersionFor(Files file) {
        return fileToVersion.get(file);
    }

    public void setVersionFor (Files file, Version version) {
        fileToVersion.put(file, version);
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
