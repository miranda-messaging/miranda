package com.ltsllc.miranda.servlet.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;

/**
 * Created by Clark on 3/10/2017.
 */
public class ClusterStatusReadyState extends State {
    public ClusterStatusReadyState (ClusterStatus clusterStatus) {
        super(clusterStatus);
    }

    public ClusterStatus getClusterStatus () {
        return (ClusterStatus) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetStatusResponse: {
                GetStatusResponseMessage getStatusResponseMessage = (GetStatusResponseMessage) message;
                nextState = processGetStatusResponseMessage (getStatusResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }

    private State processGetStatusResponseMessage (GetStatusResponseMessage getStatusResponseMessage) {
        getClusterStatus().receivedClusterStatus(getStatusResponseMessage);

        return this;
    }
}
