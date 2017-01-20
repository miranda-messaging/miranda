package com.ltsllc.miranda.messagesFile;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.SingleFile;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/11/2017.
 */
public class MessagesFile extends SingleFile<Message> {
    public MessagesFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);
    }
}
