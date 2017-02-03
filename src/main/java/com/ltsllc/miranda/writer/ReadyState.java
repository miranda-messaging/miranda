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
public class ReadyState extends State {
    public ReadyState(Consumer container) {
        super(container);
    }

    private BlockingQueue<Message> network;

    public BlockingQueue<Message> getNetwork() {
        return network;
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
            WriteSucceededMessage writeSucceededMessage = new WriteSucceededMessage(getNetwork(), filename);
            send(writeMessage.getSender(), writeSucceededMessage);
        } catch (IOException e) {
            WriteFailedMessage writeFailedMessage = new WriteFailedMessage (getNetwork(), filename, e);
            send (writeMessage.getSender(), writeFailedMessage);
        } finally {
            IOUtils.closeNoExceptions(fos);
        }


        return nextState;
    }
}
