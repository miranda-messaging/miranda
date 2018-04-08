package com.ltsllc.miranda.remoteNode.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.user.UserManager;

/**
 * Ask for the versions of all the file on behalf of a remote node.
 */
public class GetVersionsStartState extends State {
    @Override
    public State start() {
        Cluster.getInstance().sendGetVersion(getContainer());
        return null;
    }
}
