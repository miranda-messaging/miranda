package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Equivalent;
import com.ltsllc.miranda.clientinterface.basicclasses.Mergeable;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;

public class DirectoryManagerShuttingDownState<T extends Mergeable & Equivalent> extends State {
    private int numberWaitingOn;

    public int getNumberWaitingOn() {
        return numberWaitingOn;
    }

    public void setNumberWaitingOn(int numberWaitingOn) {
        numberWaitingOn = numberWaitingOn;
    }

    public DirectoryManager getDirectoryManager () {
        return (DirectoryManager) getContainer();
    }

    public DirectoryManagerShuttingDownState (DirectoryManager directoryManager) {
        super(directoryManager);
    }

    public void decrementNumberWaitingOn () {
        numberWaitingOn--;
    }

    public State start () {
        int count = 0;

        for (Object o : getDirectoryManager().getMap().values()) {
            Consumer consumer = (Consumer) o;
            consumer.sendShutdown(getDirectoryManager().getQueue(), getDirectoryManager());
            count++;
        }

        setNumberWaitingOn(count);

        return this;
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getDirectoryManager().getCurrentState();

        switch (message.getSubject()) {
            case ShutdownResponse: {
                ShutdownResponseMessage shutdownResponseMessage = (ShutdownResponseMessage) message;
                nextState = processShutdownResponseMessage(shutdownResponseMessage);
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
        decrementNumberWaitingOn();

        if (getNumberWaitingOn() < 1)
            return StopState.getInstance();
        else
            return getDirectoryManager().getCurrentState();
    }

}
