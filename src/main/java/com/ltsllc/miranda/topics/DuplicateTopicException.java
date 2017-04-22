package com.ltsllc.miranda.topics;

import com.ltsllc.miranda.MirandaException;

/**
 * Created by Clark on 4/9/2017.
 */
public class DuplicateTopicException extends MirandaException {
    public DuplicateTopicException (String message) {
        super(message);
    }
}
