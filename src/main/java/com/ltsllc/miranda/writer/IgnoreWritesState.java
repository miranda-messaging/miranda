package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 2/8/2017.
 */
public class IgnoreWritesState extends State {
    private static Logger logger = Logger.getLogger(IgnoreWritesState.class);

    public static void setLogger (Logger logger) {
        IgnoreWritesState.logger = logger;
    }

    public IgnoreWritesState (Consumer consumer) {
        super(consumer);
    }

    public State processMessage (Message m)
    {
        State nextState = this;

        switch (m.getSubject()) {
            case Write: {
                WriteMessage writeMessage = (WriteMessage) m;
                nextState = processWriteMessage(writeMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }
        }

        return nextState;
    }

    private State processWriteMessage (WriteMessage writeMessage) {
        logger.warn ("Ignoring write to " + writeMessage.getFilename());
        return this;
    }

}
