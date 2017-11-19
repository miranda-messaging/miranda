package com.ltsllc.miranda;

import com.ltsllc.miranda.node.Node;

import java.util.List;

/**
 * A {@link Quorum} that accepts acknowledge
 */
public class AcknowledgeQuorum extends Quorum {
    public AcknowledgeQuorum(List<Node> waitingFor) {
        super(waitingFor);
    }

    public boolean requiresWrites() {
        return false;
    }
}
