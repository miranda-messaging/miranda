package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;

/**
 * Created by Clark on 12/31/2016.
 */
public class ConnectMessage extends Message {
    public ConnectMessage () {
        super(Subjects.Connect);
    }

}
