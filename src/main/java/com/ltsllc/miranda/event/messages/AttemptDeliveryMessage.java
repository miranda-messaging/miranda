package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.message.Message;

public class AttemptDeliveryMessage extends Message {
    public AttemptDeliveryMessage () {
        super(Subjects.AttemptDelivery, null,null);
    }
}
