package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.manager.Manager;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/26/2017.
 */
public class ManagerShuttingDownState extends State {
    private BlockingQueue<Message> requester;

    public BlockingQueue<Message> getRequester() {
        return requester;
    }

    public Manager getManager () {
        return (Manager) getContainer();
    }

    public ManagerShuttingDownState (Manager manager, BlockingQueue<Message> requester) {
        super(manager);

        this.requester = requester;
    }

    public State processMessage(Message message) {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
            case ShutdownResponse: {
                ShutdownResponseMessage shutdownResponseMessage = (ShutdownResponseMessage) message;
                nextState = processShutdownResponseMessage (shutdownResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    public State processShutdownResponseMessage (ShutdownResponseMessage shutdownResponseMessage) {
        ShutdownResponseMessage shutdownResponseMessage2 = new ShutdownResponseMessage(getManager().getQueue(), this,
                getManager().getName());

        send(getRequester(), shutdownResponseMessage2);

        return StopState.getInstance();
    }
}
