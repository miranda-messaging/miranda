package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NameVersion;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/9/2017.
 */
public class VersionsMessage extends Message {
    private List<NameVersion> versions;

    public List<NameVersion> getVersions() {
        return versions;
    }

    public VersionsMessage (BlockingQueue<Message> senderQueue, Object sender, List<NameVersion> versions) {
        super(Subjects.Versions, senderQueue, sender);

        this.versions = versions;
    }
}
