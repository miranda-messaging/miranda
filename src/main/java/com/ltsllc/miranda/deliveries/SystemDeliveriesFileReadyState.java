package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.states.DirectoryReadyState;

/**
 * Created by Clark on 2/19/2017.
 */
public class SystemDeliveriesFileReadyState extends DirectoryReadyState {
    public SystemDeliveriesFileReadyState (SystemDeliveriesFile systemDeliveriesFile) {
        super(systemDeliveriesFile);
    }

    @Override
    public State processMessage(Message message) {
        return super.processMessage(message);
    }
}
