package com.ltsllc.miranda.main;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Electing extends State {
    public Electing (BlockingQueue<Message> queue) {
        super(queue);
    }
}
