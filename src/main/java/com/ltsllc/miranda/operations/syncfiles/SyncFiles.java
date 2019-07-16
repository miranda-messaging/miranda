package com.ltsllc.miranda.operations.syncfiles;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.operations.syncfiles.messages.GetVersionResponseMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class SyncFiles extends Operation {
    public static String NAME = "Sync files operation";
    private Map<Files, VersionNode> fileToVersionNode = new HashMap<Files, VersionNode>();
    private Map<Files,Boolean> waitingOn = new HashMap<>();

    public Map<Files, Boolean> getWaitingOn() {
        return waitingOn;
    }

    public void setWaitingOn(Map<Files, Boolean> waitingOn) {
        this.waitingOn = waitingOn;
    }

    public SyncFiles (BlockingQueue<Message> requester) {
        super(NAME, requester);
    }

    public SyncFiles () {
        super(NAME, null);
    }

    private Map<Files, VersionNode> fileToVersion = new HashMap<Files, VersionNode>();

    public VersionNode getVersionFor (Files file) {
        return fileToVersionNode.get(file);
    }

    public void setVersionNodeForFile (Files file, VersionNode versionNode) {
        VersionNode current = fileToVersionNode.get(file);
        if (current == null) {
            fileToVersionNode.put(file, new VersionNode(versionNode.getVersion(), versionNode.getNode()));
        } else if (versionNode.getVersion().isNewer(current.getVersion())) {
            VersionNode versionNode2 = new VersionNode(versionNode.getVersion(), versionNode.getNode());
            fileToVersionNode.put(file, versionNode2);
        } // if the current version is newer than versionNode then keep it
    }

    public void addVersion(Files file, Version version, Node node) {
        VersionNode current = fileToVersionNode.get(file);
        if (current == null) {
            fileToVersionNode.put(file, new VersionNode(version, node));
        } else if (version.isNewer(current.getVersion())) {
            VersionNode versionNode = new VersionNode(version, node);
            fileToVersionNode.put(file, versionNode);
        } // if the current version is newer than vesrionNode then keep it

    }


    public void addFile(Files file, GetVersionResponseMessage getVersionResponseMessage) {
        Version version = getVersionResponseMessage.getVersionFor(file);
        Node node = getVersionResponseMessage.getNode();
        addVersion(file, version, node);
    }

    public void addFiles (GetVersionResponseMessage getVersionResponseMessage) {
        addFile(Files.Cluster, getVersionResponseMessage);
        addFile(Files.Topic, getVersionResponseMessage);
        addFile(Files.Subscription, getVersionResponseMessage);
        addFile(Files.Cluster, getVersionResponseMessage);
        addFile(Files.DeliveriesList, getVersionResponseMessage);
        addFile(Files.EventList, getVersionResponseMessage);
        addFile(Files.DeliveriesList, getVersionResponseMessage);
    }

    public Node getNodeFor (Files file) {
        VersionNode versionNode = fileToVersionNode.get(file);
        return versionNode.getNode();
    }
}
