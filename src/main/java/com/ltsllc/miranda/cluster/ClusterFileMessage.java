package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/9/2017.
 */
public class ClusterFileMessage extends Message {
    private byte[] buffer;
    private Version version;

    public byte[] getBuffer() {
        return buffer;
    }

    public Version getVersion() {
        return version;
    }

    public ClusterFileMessage (BlockingQueue<Message> senderQueue, Object sender, byte[] buffer, Version version) {
        super(Subjects.ClusterFile, senderQueue, sender);

        this.buffer = buffer;
        this.version = version;
    }
}
