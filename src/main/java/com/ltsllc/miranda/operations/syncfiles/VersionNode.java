package com.ltsllc.miranda.operations.syncfiles;

import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.node.Node;

public class VersionNode {
    private Version version;
    private Node node;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public VersionNode(Version version, Node node) {
        setNode(node);
        setVersion(version);
    }

    public boolean isAfter (VersionNode versionNode) {
        return versionNode.getVersion().isAfter(versionNode.getVersion());
    }


}
