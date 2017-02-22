package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.node.NodeElement;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/9/2017.
 */
public class ClusterFileMessage extends Message {
    private List<NodeElement> file;
    private Version version;


    public Version getVersion() {
        return version;
    }

    public List<NodeElement> getFile() {
        return file;
    }

    public ClusterFileMessage (BlockingQueue<Message> senderQueue, Object sender, List<NodeElement> file, Version version) {
        super(Subjects.ClusterFile, senderQueue, sender);

        this.file = file;
        this.version = version;
    }
}
