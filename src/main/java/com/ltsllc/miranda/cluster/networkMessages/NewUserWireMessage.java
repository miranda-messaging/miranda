package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.servlet.objects.UserObject;

/**
 * Created by Clark on 4/12/2017.
 */
public class NewUserWireMessage extends WireMessage {
    private UserObject userObject;

    public UserObject getUserObject() {
        return userObject;
    }

    public NewUserWireMessage (UserObject userObject) {
        super(WireSubjects.NewUser);

        this.userObject = userObject;
    }
}
