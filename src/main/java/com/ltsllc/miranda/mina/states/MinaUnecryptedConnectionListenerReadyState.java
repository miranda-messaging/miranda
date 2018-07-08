package com.ltsllc.miranda.mina.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.mina.MinaUnencryptedConnectionListener;

public class MinaUnecryptedConnectionListenerReadyState extends State {
    public MinaUnecryptedConnectionListenerReadyState (MinaUnencryptedConnectionListener minaUnencryptedConnectionListener) {
        super(minaUnencryptedConnectionListener);
    }
}
