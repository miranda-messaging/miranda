package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Subsystem;
import com.ltsllc.miranda.node.ConnectedMessage;
import com.ltsllc.miranda.node.Node;

/**
 * A logical grouping of {@Link Node}.
 *
 * <P>
 * This class allows the rest of the system to treat a cluster like a single unit.
 * For example the system can "tell" the cluster about a new message and
 * let the class worry about distributing it.
 * </P>
 * Created by Clark on 12/31/2016.
 */
public class Cluster extends Consumer{
    public Cluster (String filename) {
        super("Cluster");
        setCurrentState(new Ready(this));
        setFilename(filename);
    }

    private Node[] nodes;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename (String s) {
        filename = s;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public State prcessMessage (Message m) {
        switch (m.getSubject())
        {
            case Connect: {
                processConnect();
                break;
            }
        }

        return getCurrentState();
    }


    /**
     * Tell the nodes in the cluster to connect.
     */
    private void processConnect () {
        for (Node n : nodes) {
            send(n.getQueue(), new ConnectMessage());
        }
    }
}
