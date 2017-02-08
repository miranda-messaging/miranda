package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class ClusterFileGettingVersionState extends State {
    private BlockingQueue<Message> notify;

    public BlockingQueue<Message> getNotify() {
        return notify;
    }

    public ClusterFileGettingVersionState(Consumer consumer, BlockingQueue<Message> notify) {
        super(consumer);
        this.notify = notify;
    }
}
