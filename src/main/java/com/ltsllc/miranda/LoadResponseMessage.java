package com.ltsllc.miranda;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/10/2017.
 */
public class LoadResponseMessage extends Message {
    private List data;

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public LoadResponseMessage (BlockingQueue<Message> senderQueue, Object sender, List data) {
        super(Subjects.LoadResponse, senderQueue, sender);

        this.data = data;
    }
}
