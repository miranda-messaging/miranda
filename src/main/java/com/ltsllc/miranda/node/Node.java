package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Subsystem;
import com.ltsllc.miranda.cluster.ConnectMessage;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class Node extends Consumer
{
    private static Logger logger = Logger.getLogger(Node.class);

    public Node (String name) {
        super(name);
    }

    public void connect (Message m)
    {
        try {
            m.respond(new ConnectMessage());
        } catch (InterruptedException e) {
            logger.error ("Interrupted while trying to connect",e );
        }
    }

    public State processMessage (Message m)
    {
        if (m instanceof ConnectedMessage)
            connect(m);

        return getCurrentState();
    }
}
