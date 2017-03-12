package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.node.NameVersion;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class VersionMessage extends Message {
    private NameVersion nameVersion;

    public NameVersion getNameVersion() {
        return nameVersion;
    }



    public VersionMessage (BlockingQueue<Message> senderQueue, Object sender, NameVersion nameVersion) {
        super(Subjects.Version, senderQueue, sender);

        this.nameVersion = nameVersion;
    }
}
