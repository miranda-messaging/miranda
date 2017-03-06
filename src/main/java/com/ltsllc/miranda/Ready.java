package com.ltsllc.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/30/2016.
 */
public class Ready extends State {
    public Ready () {
        super(null);
    }


    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject())
        {
            case NewDelivery :
                processDelivery(m);
                break;

            case NewMessage:
                processNewMessage(m);
                break;

            case NewSubscription:
                processSubscription(m);
                break;

            case Ballot:

                break;
        }

        return nextState;
    }

    public void processDelivery (Message m)
    {}

    public void processNewMessage (Message m)
    {}

    public void processSubscription (Message m)
    {}

    public void processBallot (Message m)
    {}


}
