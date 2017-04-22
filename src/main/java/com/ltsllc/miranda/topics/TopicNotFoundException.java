package com.ltsllc.miranda.topics;

import com.ltsllc.miranda.MirandaException;

/**
 * Created by Clark on 4/10/2017.
 */
public class TopicNotFoundException extends MirandaException {
    public TopicNotFoundException (String message) {
        super(message);
    }
}
