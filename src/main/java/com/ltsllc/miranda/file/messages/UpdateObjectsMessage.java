package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class UpdateObjectsMessage extends Message {
    private List updatedObjects;

    public List getUpdatedObjects() {
        return updatedObjects;
    }

    public UpdateObjectsMessage (BlockingQueue<Message> senderQueue, Object sender, List updatedObjects) {
        super(Subjects.UpdateObjects, senderQueue, sender);

        this.updatedObjects = updatedObjects;
    }
}
