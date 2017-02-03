package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Writer extends Consumer {
    public Writer (BlockingQueue<Message> queue) {
        super("Writer");
        setQueue(queue);
        setCurrentState(new ReadyState(this));
    }
}
