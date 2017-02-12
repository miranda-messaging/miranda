package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.VersionMessage;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class GettingVersionState extends State {
    private static Logger logger = Logger.getLogger(GettingVersionState.class);

    private BlockingQueue<Message> notify;
    private ClusterFile clusterFile;

    public GettingVersionState (Consumer consumer, ClusterFile clusterFile, BlockingQueue<Message> notify) {
        super(consumer);
        this.clusterFile = clusterFile;
        this.notify = notify;
    }

    public BlockingQueue<Message> getNotify() {
        return notify;
    }

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage (versionMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processVersionMessage (VersionMessage versionMessage) {
        return this;
    }
}
