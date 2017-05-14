package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.ReadResponseMessage;

/**
 * Created by Clark on 5/14/2017.
 */
abstract public class LoadingState extends State {
    abstract public State getReadyState();

    public SingleFile getSingleFile() {
        return (SingleFile) getContainer();
    }

    public LoadingState (SingleFile singleFile) {
        super(singleFile);
    }

    public State processMessage (Message message) {
        State nextState = getSingleFile().getCurrentState();

        switch (message.getSubject()) {
            case ReadResponse : {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage(readResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processReadResponseMessage (ReadResponseMessage readResponseMessage) {
        State nextState = getSingleFile().getCurrentState();

        if (readResponseMessage.getResult() == Results.Success) {
            getSingleFile().processData(readResponseMessage.getData());
            nextState = getReadyState();
        } else {
            Panic panic = new Panic ("Error trying to load file", Panic.Reasons.ErrorLoadingFile, readResponseMessage.getAdditionalInfo());
            Miranda.getInstance().panic(panic);
        }

        return nextState;
    }
}
