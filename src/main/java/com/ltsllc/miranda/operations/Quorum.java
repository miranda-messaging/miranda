package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.node.Node;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 6/13/2017.
 */
abstract public class Quorum {
    abstract public boolean requiresWrites();

    private static Logger logger = Logger.getLogger(Quorum.class);

    private List<Node> waitingFor;
    private List<Node> responded;

    public Quorum (List<Node> waitingFor) {
        this.waitingFor = waitingFor;
        this.responded = new ArrayList<Node>();
    }

    public List<Node> getResponded() {
        return responded;
    }

    public List<Node> getWaitingFor() {
        return waitingFor;
    }

    public boolean complete () {
        return getResponded().size() > getWaitingFor().size();
    }

    public void addResponse (Node node, WireResponse response) {
        if (response.getResult() == Results.Acknowleged && !requiresWrites()) {
            moveWaitingToResponded(node);
        } else if (response.getResult() == Results.Written && requiresWrites()) {
            moveWaitingToResponded(node);
        }

        if (complete()) {
            logger.info ("Got response, quorum complete!");
        } else {
            logger.info("Got response, still waiting for " + getWaitingFor().size() + " nodes");
        }
    }

    public void moveWaitingToResponded(Node node) {
        getWaitingFor().remove(node);
        getResponded().add(node);
    }
}
