package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * Created by Clark on 4/13/2017.
 */
public class DeleteUserWireMessage extends WireMessage {
    private String name;

    public String getName() {
        return name;
    }

    public DeleteUserWireMessage (String name) {
        super(WireSubjects.DeleteUser);

        this.name = name;
    }
}
