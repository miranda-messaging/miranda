package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NameVersion;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class RemoteVersionMessage extends Message {
    private BlockingQueue<Message> node;
    private NameVersion version;

    public RemoteVersionMessage(BlockingQueue<Message> senderQueue, Object sender, BlockingQueue<Message> node, NameVersion version) {
        super(Subjects.RemoteVersion, senderQueue, sender);

        this.node = node;
        this.version = version;
    }

    public NameVersion getVersion() {
        return version;
    }

    public BlockingQueue<Message> getNode() {
        return node;
    }
}
