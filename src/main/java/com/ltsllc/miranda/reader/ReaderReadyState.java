package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 5/3/2017.
 */
public class ReaderReadyState extends State {
    public Reader getReader () {
        return (Reader) getContainer();
    }

    public ReaderReadyState (Reader reader) {
        super(reader);
    }

    public State processMessage (Message message) {
        State nextState = getReader().getCurrentState();

        switch (message.getSubject()) {
            case Read : {
                ReadMessage readMessage = (ReadMessage) message;
                nextState = processReadMessage (readMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processReadMessage (ReadMessage readMessage) {
        Reader.ReadResult result = getReader().read(readMessage.getFilename());

        ReadResponseMessage response = new ReadResponseMessage(getReader().getQueue(), this, result.result,
                result.data);

        return getReader().getCurrentState();
    }
}
