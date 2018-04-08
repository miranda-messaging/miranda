package com.ltsllc.miranda.remoteNode;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.remoteNode.states.RemoteNodeStartState;

/**
 * A Node over the network.
 */
public class RemoteNode extends Consumer {
    private Handle handle;

    public Handle getHandle() {
        return handle;
    }

    public void setHandle(Handle handle) {
        this.handle = handle;
    }

    public RemoteNode (Handle handle) {
        setHandle(handle);
        setCurrentState(new RemoteNodeStartState());
    }
}
