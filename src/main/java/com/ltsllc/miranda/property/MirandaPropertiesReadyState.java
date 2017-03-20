package com.ltsllc.miranda.property;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.FileChangedMessage;
import com.ltsllc.miranda.file.MirandaFileReadyState;
import com.ltsllc.miranda.file.SingleFileReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;

/**
 * Created by Clark on 3/19/2017.
 */
public class MirandaPropertiesReadyState extends MirandaFileReadyState {
    private static Logger logger = Logger.getLogger(MirandaPropertiesReadyState.class);

    public MirandaProperties getMirandaProperties () {
        return (MirandaProperties) getContainer();
    }

    public MirandaPropertiesReadyState (MirandaProperties properties)
    {
        super(properties);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case WriteSucceeded: {
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

    public State processWriteFailedMessage (WriteFailedMessage writeFailedMessage) {
        Panic panic = new Panic ("Could not write proerties file: " + getMirandaProperties().getFilename(), writeFailedMessage.getCause(), Panic.Reasons.CouldNotWrite);
        Miranda.getInstance().panic(panic);
        logger.error ("Ignoring write failed for properties file: " + getMirandaProperties().getFilename(), writeFailedMessage.getCause());

        return getMirandaProperties().getCurrentState();
    }

    public State processFileChangedMessage (FileChangedMessage fileChangedMessage) {
        Miranda.getInstance().sendNewProperties(getMirandaProperties().getQueue(), this, getMirandaProperties());

        return getMirandaProperties().getCurrentState();
    }

}
