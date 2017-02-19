package com.ltsllc.miranda.messagesFile;

import com.ltsllc.miranda.file.DirectoryReadyState;
import com.ltsllc.miranda.messagesFile.SystemMessages;

/**
 * Created by Clark on 2/19/2017.
 */
public class SystemMessagesReadyState extends DirectoryReadyState {
    public SystemMessagesReadyState (SystemMessages systemMessages) {
        super(systemMessages);
    }
}
