package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 3/18/2017.
 */
public class SingleFileStoppingState extends State {
    private static Logger logger = Logger.getLogger(SingleFileStoppingState.class);

    private int numberOfWriteFailures;

    public int getNumberOfWriteFailures() {
        return numberOfWriteFailures;
    }

    public void setNumberOfWriteFailures(int numberOfWriteFailures) {
        this.numberOfWriteFailures = numberOfWriteFailures;
    }

    public void incrementNumberOfWriteFailures () {
        numberOfWriteFailures++;
    }

    public SingleFile getSingleFile () {
        return (SingleFile) getContainer();
    }

    public SingleFileStoppingState(SingleFile singleFile) {
        super(singleFile);
    }

    public State processMessage (Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case WriteSucceeded: {
                WriteSucceededMessage writeSucceededMessage = (WriteSucceededMessage) message;
                nextState = processWriteSucceededMessage(writeSucceededMessage);
                break;
            }

            case WriteFailed: {
                WriteFailedMessage writeFailedMessage = (WriteFailedMessage) message;
                nextState = processWriteFailedMessage(writeFailedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processWriteSucceededMessage (WriteSucceededMessage writeSucceededMessage) {
        logger.info (getSingleFile() + " stopping");
        return StopState.getInstance();
    }

    public State processWriteFailedMessage (WriteFailedMessage writeFailedMessage) {
        incrementNumberOfWriteFailures();

        int maxFailures = Miranda.properties.getIntProperty(MirandaProperties.PROPERTY_MAX_WRITE_FAILURES);

        if (getNumberOfWriteFailures() >= maxFailures) {
            String message = "Could not write out " + getSingleFile().getFilename() + " after " + getNumberOfWriteFailures()
                    + " tries.  Giving up.";
            logger.error(message, writeFailedMessage.getCause());
        }

        return this;
    }
}
