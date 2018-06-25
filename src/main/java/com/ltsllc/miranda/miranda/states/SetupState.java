package com.ltsllc.miranda.miranda.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * A state that allows the creation of a KeyStore and a Truststore.
 *
 * <p>
 *    In this state, the system cannot save HTTP messages or most of what it is designed to do.
 *    The system just waits around for someone to ask it to create tne required files.
 * </p>
 */
public class SetupState extends State {
    public SetupState (Consumer container) throws MirandaException {
        super(container);
    }
}
