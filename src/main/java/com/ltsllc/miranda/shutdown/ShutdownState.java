package com.ltsllc.miranda.shutdown;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.panics.ShutdownPanic;

import java.util.concurrent.BlockingQueue;

/**
 * This represents a synchronous shutdown.  An object in this state tries to write out any data and then reply to the
 * shutdown message.
 *
 * <p>
 *     The default behavior is to respond immediately and await a {@link com.ltsllc.miranda.miranda.messages.StopMessage}.
 * </p>
 */
public class ShutdownState extends State {
    private BlockingQueue<Message> initiator;

    public BlockingQueue<Message> getInitiator() {
        return initiator;
    }

    public void setInitiator(BlockingQueue<Message> initiator) {
        this.initiator = initiator;
    }

    public ShutdownState (Consumer consumer, BlockingQueue<Message> initiator) {
        super(consumer);
        setInitiator(initiator);
    }

    /**
     * The default behavior is to immediately send a reply and await a {@link com.ltsllc.miranda.miranda.messages.StopMessage}.
     *
     * <p>
     *     If an exception occurs, the method will panic Miranda.
     * </p>
     *
     * @return an instace of {@link StopState}
     */
    public State start () {
        try {
            sendReadyToShutdown();
            StopState stopState = new StopState();
            return this;
        } catch (MirandaException e) {
            Panic panic = new ShutdownPanic(ShutdownPanic.ShutdownReasons.Exception, e);
            Miranda.panicMiranda(panic);
            return this;
        }
    }

    public void sendReadyToShutdown () {
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(getContainer().getQueue(), this,
                getContainer().getName());

        send (shutdownResponseMessage, initiator);
    }

}
