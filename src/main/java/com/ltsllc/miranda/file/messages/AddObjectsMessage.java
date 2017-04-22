package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class AddObjectsMessage extends Message {
    private List objects;

    public List getObjects() {
        return objects;
    }

    public AddObjectsMessage (BlockingQueue<Message> senderQueue, Object sender, List objects) {
        super(Subjects.AddObjects, senderQueue, sender);

        this.objects = objects;
    }
}
