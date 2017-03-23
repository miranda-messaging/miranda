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

    public boolean equals (Object o) {
        if (null == o || !(o instanceof JoinResponseWireMessage))
            return false;

        JoinResponseWireMessage other = (JoinResponseWireMessage) o;
        return getResult().equals(other.getResult());
    }

    public String toString () {
        String s = "joinResponse{" + getResult() + "}";
        return s;
    }
}
