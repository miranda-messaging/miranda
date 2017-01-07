package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class TopicFile  extends MirandaFile<Topic> {
    public TopicFile (BlockingQueue<Message> queue, String s) {
        super(queue, s);
    }
}
