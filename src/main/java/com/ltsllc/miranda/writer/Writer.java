package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Writer extends Consumer {
    public Writer () {
        super("writer");
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
        // setCurrentState(new WriterReadyState(this));
        setCurrentState(new IgnoreWritesState(this));
    }

    public Writer (BlockingQueue<Message> queue) {
        super("Writer");
        setQueue(queue);
        // setCurrentState(new WriterReadyState(this));
        setCurrentState(new IgnoreWritesState(this));
    }
}
