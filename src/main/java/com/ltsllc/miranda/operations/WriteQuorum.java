package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.node.Node;

import java.util.List;

/**
 * Created by Clark on 6/13/2017.
 */
public class WriteQuorum extends Quorum {
    public WriteQuorum (List<Node> waitingFor) {
        super(waitingFor);
    }

    public boolean requiresWrites () {
        return true;
    }
}
