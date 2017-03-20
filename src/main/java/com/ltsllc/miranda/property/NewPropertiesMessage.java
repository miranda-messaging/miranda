package com.ltsllc.miranda.property;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/19/2017.
 */
public class NewPropertiesMessage extends Message {
    private MirandaProperties mirandaProperties;

    public MirandaProperties getMirandaProperties() {
        return mirandaProperties;
    }

    public NewPropertiesMessage (BlockingQueue<Message> senderQueue, Object sender, MirandaProperties mirandaProperties) {
        super(Subjects.NewProperties, senderQueue, sender);

        this.mirandaProperties = mirandaProperties;
    }
}
