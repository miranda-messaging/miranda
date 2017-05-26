package com.ltsllc.miranda.operations.newNode;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.operations.Operation;

import java.util.concurrent.BlockingQueue;

/**
 * Created by clarkhobbie on 5/24/17.
 */
public class NewNodeOperation extends Operation {
    public static final String NAME = "new node operation";

    public NewNodeOperation (BlockingQueue<Message> requester) {
        super (NAME, requester);
    }
}
