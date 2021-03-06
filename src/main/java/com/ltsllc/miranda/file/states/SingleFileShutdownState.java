package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.MirandaObject;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;
import com.ltsllc.miranda.writer.WriteResponseMessage;
import org.apache.log4j.Logger;


import java.util.concurrent.BlockingQueue;

/**
 * The state a {@link SingleFile} takes when it receives a shutDown message
 *
 * <p>
 *     The state issues a write and waits for a reply.
 *     When it gets a reply it sends a shutDownResponse and enters the stop state.
 * </p>
 * @param <E>
 */
public class SingleFileShutdownState<E extends MirandaObject> extends State {
    private static Logger LOGGER = Logger.getLogger(SingleFileShutdownState.class);

    private BlockingQueue<Message> initiator;

    public BlockingQueue<Message> getInitiator() {
        return initiator;
    }

    public void setInitiator(BlockingQueue<Message> initiator) {
        this.initiator = initiator;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void setLOGGER(Logger LOGGER) {
        SingleFileShutdownState.LOGGER = LOGGER;
    }

    public SingleFile<E> getSingleFile () {
        return (SingleFile<E>) getContainer();
    }

    public SingleFileShutdownState(BlockingQueue<Message> initiator) {
        setInitiator(initiator);
    }

    public State start () {
        getSingleFile().getWriter().sendWrite(getSingleFile().getQueue(), this, getSingleFile().getFilename(),
                getSingleFile().getBytes());
        return this;
    }

    public State processMessage (Message message) {
        try {
            State nextState = null;

            switch (message.getSubject()) {
                case WriteResponse: {
                    WriteResponseMessage writeResponseMessage = (WriteResponseMessage) message;
                    nextState = processWriteResponseMessage(writeResponseMessage);
                    break;
                }
                default: {
                    nextState = super.processMessage(message);
                    break;
                }
            }

            return nextState;
        } catch (MirandaException e) {
            Panic panic = new Panic(e, Panic.Reasons.ExceptionInProcessMessage);
            Miranda.panicMiranda(panic);
            return this;
        }
    }

    public State processWriteResponseMessage (WriteResponseMessage writeResponseMessage) {
        return StopState.getInstance();
    }
}
