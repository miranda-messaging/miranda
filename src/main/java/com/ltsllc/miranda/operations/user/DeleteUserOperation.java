package com.ltsllc.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.topics.TopicManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class DeleteUserOperation extends Operation {
    private String user;
    private List<String> subsystems;

    public String getUser() {
        return user;
    }

    public List<String> getSubsystems() {
        return subsystems;
    }

    public DeleteUserOperation (BlockingQueue<Message> requester, Session session, String user) {
        super("delete user operation", requester, session);

        DeleteUserOperationReadyState readyState = new DeleteUserOperationReadyState(this);
        setCurrentState(readyState);

        this.user = user;
        this.subsystems = new ArrayList<String>();
        this.subsystems.add(SubscriptionManager.NAME);
        this.subsystems.add(TopicManager.NAME);
    }

    public void start () {
        super.start();

        Miranda.getInstance().getTopicManager().sendOwnerQueryMessage(getQueue(), this, getUser());
        Miranda.getInstance().getSubscriptionManager().sendOwnerQueryMessage(getQueue(), this, getUser());
    }

    public void subsystemResponded (String name) {
        String subsystem = null;

        for (String s : getSubsystems()) {
            if (name.equals(s))
                subsystem = s;
        }

        if (null != subsystem)
            getSubsystems().remove(subsystem);
    }
}
