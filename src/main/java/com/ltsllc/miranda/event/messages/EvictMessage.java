package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Message;

/**
 * Created by Clark on 6/12/2017.
 */
public class EvictMessage extends Message {
    public EvictMessage() {
        super(Subjects.Evict, null, null);
    }
}
