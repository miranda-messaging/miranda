package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.util.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/3/2017.
 */
public class WriterReadyState extends State {
    public Writer getWriter () {
        return (Writer) getContainer();
    }

    public WriterReadyState(Writer writer) {
        super(writer);
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
        try{
            getWriter().write(writeMessage.getFilename(), writeMessage.getBuffer());
            WriteSucceededMessage writeSucceededMessage = new WriteSucceededMessage(getWriter().getQueue(), writeMessage.getFilename(), this);
            writeMessage.reply(writeSucceededMessage);
        } catch (IOException e) {
            WriteFailedMessage writeFailedMessage = new WriteFailedMessage(getWriter().getQueue(), writeMessage.getFilename(), e, this);
            writeMessage.reply(writeFailedMessage);
        }

        return this;
    }
}
