package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.manager.Manager;

public class ManagerState extends State {
    public Manager getManager () {
        return (Manager) getContainer();
    }

    public ManagerState (Manager manager) {
        super(manager);
    }
}
