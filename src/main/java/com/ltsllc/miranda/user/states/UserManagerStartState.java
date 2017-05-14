package com.ltsllc.miranda.user.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.manager.ManagerLoadingState;
import com.ltsllc.miranda.user.UserManager;

/**
 * Created by Clark on 5/14/2017.
 */
public class UserManagerStartState extends ManagerLoadingState {
    public UserManager getUserManager () {
        return (UserManager) getContainer();
    }

    public UserManagerStartState (UserManager userManager) {
        super(userManager);
    }

    public State getReadyState () {
        return new UserManagerReadyState(getUserManager());
    }
}
