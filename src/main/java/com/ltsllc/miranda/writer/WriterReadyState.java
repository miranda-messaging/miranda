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
    public WriterReadyState(Consumer container) {
        super(container);
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
        State nextState = this;

        String filename = writeMessage.getFilename();
        byte[] buffer = writeMessage.getBuffer();

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(writeMessage.getFilename());
            fos.write(buffer);
            WriteSucceededMessage writeSucceededMessage = new WriteSucceededMessage(writeMessage.getSender(), filename, this);
            send(writeMessage.getSender(), writeSucceededMessage);
        } catch (IOException e) {
            WriteFailedMessage writeFailedMessage = new WriteFailedMessage (writeMessage.getSender(), filename, e, this);
            send (writeMessage.getSender(), writeFailedMessage);
        } finally {
            IOUtils.closeNoExceptions(fos);
        }


        return nextState;
    }
}
