package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;

/**
 * Created by Clark on 1/2/2017.
 */
public class ConnectedMessage extends Message {
    public ConnectedMessage ()
    {
        super(Subjects.Connected);
    }
}
