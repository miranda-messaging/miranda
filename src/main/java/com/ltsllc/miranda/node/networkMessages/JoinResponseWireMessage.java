package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 3/11/2017.
 */
public class JoinResponseWireMessage extends WireMessage {
    public enum Responses {
        Success,
        Failure
    }

    private Responses result;

    public Responses getResult() {
        return result;
    }

    public JoinResponseWireMessage (Responses result) {
        super(WireSubjects.JoinResponse);

        this.result = result;
    }

}
