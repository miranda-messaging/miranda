package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

/**
 * Created by Clark on 12/31/2016.
 */
public class ConnectedMessage extends Message {
    public ConnectedMessage()
    {
        super(Subjects.Connected);
    }
}
