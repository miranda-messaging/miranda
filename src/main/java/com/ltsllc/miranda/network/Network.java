package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.messages.*;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * This class takes care of mapping from integer handles to Handle objects.
 */
abstract public class Network extends Consumer {
    abstract public Handle basicConnectTo (ConnectToMessage connectToMessage) throws NetworkException;

    private static Logger logger = Logger.getLogger(NetworkListener.class);
    private static Network ourInstance;

    private Map<Integer, Handle> integerToHandle = new HashMap<Integer, Handle>();
    private int handleCount = 0;
    private long lastPanic;

    public Network () {
        super("network");

        this.lastPanic = -1;

        NetworkReadyState networkReadyState = new NetworkReadyState(this);
        setCurrentState(networkReadyState);
    }

    public static Network getInstance() {
        return ourInstance;
    }

    protected static synchronized void setInstance (Network network) {
        if (null == ourInstance) {
            ourInstance = network;
        }
    }

    public Handle getHandle (int handle) {
        return integerToHandle.get(handle);
    }

    public Map<Integer, Handle> getHandleMap () {
        return integerToHandle;
    }

    public int nextHandle () {
        return handleCount++;
    }

    public void setHandle (int handle, Handle theHandle) {
        integerToHandle.put(handle, theHandle);
    }

    public void clearHandle (int handle)
    {
        integerToHandle.put(handle, null);
    }

    public void send (SendMessageMessage sendMessageMessage) {
        Handle handle = getHandle(sendMessageMessage.getHandle());
        if (null == handle) {
            UnknownHandleMessage unknownHandleMessage = new UnknownHandleMessage(getQueue(), this, sendMessageMessage.getHandle());
            sendMessageMessage.reply(unknownHandleMessage);
        }

        SendMessageMessage sendMessageMessage2 = new SendMessageMessage(getQueue(),this, sendMessageMessage.getHandle(), sendMessageMessage.getContent());
        send(sendMessageMessage2, handle.getQueue());
    }

    public void connect (ConnectToMessage connectToMessage) {
        try {
            Handle handle = basicConnectTo(connectToMessage);

            if (null != handle) {
                int handleValue = nextHandle();
                setHandle(handleValue, handle);
            }
        } catch (NetworkException e) {
            ConnectFailedMessage message = new ConnectFailedMessage(getQueue(), this, e.getCause());
            connectToMessage.reply(message);
        }
    }


    public void disconnect (CloseMessage disconnectMessage) {
        Handle handle = getHandle(disconnectMessage.getHandle());

        if (null == handle) {
            UnknownHandleMessage unknownHandleMessage = new UnknownHandleMessage(getQueue(), this, disconnectMessage.getHandle());
            disconnectMessage.reply(unknownHandleMessage);
        }

        handle.close();

        setHandle(disconnectMessage.getHandle(), null);

        DisconnectedMessage disconectedMessage = new DisconnectedMessage(getQueue(), this);
        disconnectMessage.reply(disconectedMessage);
    }

    /**
     * Something Very Bad happend.  Decide if we should shut down.
     * @param panic The problem that caused the panic
     * @return true if we should shut down, false otherwise
     */
    public boolean panic (Panic panic) {
        //
        // if one of our connections failed, and other connections are working,
        // then try to keep going
        //
        if (
                panic.getReason() == Panic.Reasons.NetworkThreadCrashed
                && integerToHandle.size() > 1)
        {
            return false;
        }

        //
        // if we can't talk to anyone, ask the system what to do
        //
        else if (panic.getReason() == Panic.Reasons.NetworkThreadCrashed)
        {
            Panic newPanic = new Panic(panic.getCause(), Panic.Reasons.Network);
            boolean decisionPanic = Miranda.getInstance().panic(newPanic);

            if (decisionPanic) {
                NetworkPanicState panicState = new NetworkPanicState(this);
                setCurrentState(panicState);
                return decisionPanic;
            }
        }

        return false;
    }

    /**
     * Close the connection, without sending a sign-off.
     *
     * @param handleID The handle to disconnect from.
     */
    public void forceDisconnect (int handleID) {
        Handle handle = getHandle(handleID);

        if (null != handle) {
            handle.panic();
            clearHandle(handleID);
        }
    }

    /**
     * This is called when the {@link NetworkListener} gets a new connection.
     *
     * <P>
     *     The handle rturned can be used in susequent {@link SendMessageMessage} and
     *     {@link CloseMessage}s.
     * </P>
     *
     * @param handle The new connection.
     * @return The handle for the new connection.
     */
    public int newConnection (Handle handle) {
        int handleId = nextHandle();

        setHandle(handleId, handle);

        return handleId;
    }


    /**
     * Tell the network to close a connection.
     */
    public void sendClose (BlockingQueue<Message> senderQueue, Object sender, int handle) {
        CloseMessage closeMessage = new CloseMessage(senderQueue, sender, handle);
        sendToMe(closeMessage);
    }

    /**
     * Ask the network to send a message.
     */
    public void sendMessage (BlockingQueue<Message> senderQueue, Object sender, int handle, WireMessage wireMessage) {
        SendNetworkMessage sendNetworkMessage = new SendNetworkMessage(senderQueue, sender, wireMessage, handle);
        sendToMe(sendNetworkMessage);
    }

    /**
     * Ask the network to create a connection to someone
     */
    public void sendConnect(BlockingQueue<Message> senderQueue, Object sender, String host, int port) {
        ConnectToMessage connectToMessage = new ConnectToMessage(host, port, senderQueue, sender);
        sendToMe(connectToMessage);
    }

    /**
     * This is called when we have successfully made a connection.
     *
     * @param handleID The handle ID
     * @param handle The handle that it maps to
     */
    public void mapHandle (int handleID, Handle handle) {
        integerToHandle.put(handleID, handle);
    }
}
