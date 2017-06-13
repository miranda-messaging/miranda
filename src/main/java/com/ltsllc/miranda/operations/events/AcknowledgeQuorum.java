package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.operations.Quorum;

import java.util.List;

/**
 * Created by Clark on 6/13/2017.
 */
public class AcknowledgeQuorum extends Quorum {
    public AcknowledgeQuorum(List<Node> waitingFor) {
        super(waitingFor);
    }

    public boolean requiresWrites () {
        return false;
    }
}
