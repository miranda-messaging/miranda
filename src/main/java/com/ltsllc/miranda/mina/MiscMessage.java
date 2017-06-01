package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * Created by Clark on 5/31/2017.
 */
public class MiscMessage extends WireMessage {
    private String text;

    public String getText() {
        return text;
    }

    public MiscMessage(String text) {
        super(WireSubjects.Misc);
        this.text = text;
    }
}
