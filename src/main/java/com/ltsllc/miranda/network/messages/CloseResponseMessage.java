package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/3/2017.
 */
public class CloseResponseMessage extends Message {
    private int handle;
    private Results result;

    public int getHandle() {
        return handle;
    }

    public Results getResult() {
        return result;
    }

    public CloseResponseMessage (BlockingQueue<Message> senderQueue, Object sender, int handle, Results result) {
        super(Subjects.CloseResponse, senderQueue, sender);

        this.handle = handle;
        this.result = result;
    }
}
