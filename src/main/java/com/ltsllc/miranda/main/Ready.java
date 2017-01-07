package com.ltsllc.miranda.main;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/30/2016.
 */
public class Ready extends State {
    public Ready (BlockingQueue<Message> queue) {
        super(queue);
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

            case Election:
                nextState = processElection(m);
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


    public State processElection (Message m)
    {
        return new Electing(getQueue());
    }
}
