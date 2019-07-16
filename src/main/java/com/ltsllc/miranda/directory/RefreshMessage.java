package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.Message.Subjects.Refresh;

public class RefreshMessage extends Message {
    public RefreshMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super (Refresh, senderQueue, sender);
    }
}
