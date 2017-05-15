package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 4/16/2017.
 */
public class OperationReadyState extends State {
    private static Logger logger = Logger.getLogger(OperationReadyState.class);

    public OperationReadyState (Consumer consumer) {
        super (consumer);
    }

    public State processMessage (Message message) {
        logger.error (this + " does not understand " + message + ".  Terminating.", message.getWhere());
        return StopState.getInstance();
    }
}
