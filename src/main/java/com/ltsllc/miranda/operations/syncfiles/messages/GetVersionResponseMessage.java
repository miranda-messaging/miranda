package com.ltsllc.miranda.operations.syncfiles.messages;

import com.ltsllc.miranda.Message;

import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class GetVersionResponseMessage extends Message {
    private Map<Files, Version> versions = new HashMap<>();
    private Node node;

    public GetVersionResponseMessage (BlockingQueue senderQueue, Object sender, Map<Files, Version> versions,
    Node node) {
        super(Subjects.GetVersionsResponse, senderQueue,sender);
        setVersions(versions);
        setNode(node);
    }

    public Map<Files, Version> getVersions() {
        if (versions == null)
            versions = new HashMap<>();
        return versions;
    }

    public void setVersions(Map<Files, Version> versions) {
        this.versions = versions;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setVersionFor (Files file, Version version) {
        getVersions().put(file, version);
    }

    public Version getVersionFor(Files file) {
        return getVersions().get(file);
    }


}
