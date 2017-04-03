package com.ltsllc.miranda.event;

import com.ltsllc.miranda.file.states.DirectoryReadyState;

/**
 * Created by Clark on 2/19/2017.
 */
public class SystemMessagesReadyState extends DirectoryReadyState {
    public SystemMessagesReadyState (SystemMessages systemMessages) {
        super(systemMessages);
    }
}
