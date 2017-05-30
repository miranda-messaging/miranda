package com.ltsllc.miranda.newMina;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.*;
import com.ltsllc.miranda.network.messages.*;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by clarkhobbie on 5/30/17.
 */
public abstract class NewNetwork extends Consumer {
    abstract public Handle basicConnectTo(String host, int port) throws Exception;
    abstract public Handle createHandle(Object o);

    private static Logger logger = Logger.getLogger(NetworkListener.class);
    private static Network ourInstance;

    private Map<Integer, Handle> integerToHandle = new HashMap<Integer, Handle>();
    private int handleCount = 0;
    private String truststorePassword;
    private String keystorePassword;

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }

    public void setTruststorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
    }


    public Handle getHandle(int handle) {
        return integerToHandle.get(handle);
    }

    public Map<Integer, Handle> getHandleMap() {
        return integerToHandle;
    }

    public int nextHandle() {
        return handleCount++;
    }

    public void setHandle(int handle, Handle theHandle) {
        integerToHandle.put(handle, theHandle);
    }

    public int getHandleCount() {
        return handleCount;
    }

    public void clearHandle(int handle) {
        integerToHandle.put(handle, null);
    }

    public void connect(ConnectToMessage connectToMessage) {
        try {
            Handle handle = basicConnectTo(connectToMessage.getHost(), connectToMessage.getPort());

            if (null == handle) {
                ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(getQueue(), this, null);
                connectToMessage.reply(connectFailedMessage);
            } else {
                int handleId = nextHandle();
                setHandle(handleId, handle);

                ConnectSucceededMessage connectSucceededMessage = new ConnectSucceededMessage(getQueue(), this, handleId);
                connectToMessage.reply(connectSucceededMessage);
            }
        } catch (Exception e) {
            ConnectFailedMessage message = new ConnectFailedMessage(getQueue(), this, e.getCause());
            connectToMessage.reply(message);
        }
    }


    public void disconnect(CloseMessage closeMessage) {
        Handle handle = getHandle(closeMessage.getHandle());

        if (null == handle) {
            UnknownHandleMessage unknownHandleMessage = new UnknownHandleMessage(getQueue(), this, closeMessage.getHandle());
            closeMessage.reply(unknownHandleMessage);
        } else {
            handle.close();

            setHandle(closeMessage.getHandle(), null);

            DisconnectedMessage disconnectedMessage = new DisconnectedMessage(getQueue(), this, closeMessage.getHandle());
            closeMessage.reply(disconnectedMessage);
        }
    }

    /**
     * Something Very Bad happend.  Decide if we should shut down.
     *
     * @param panic The problem that caused the panic
     * @return true if we should shut down, false otherwise
     */
    public boolean panic(Panic panic) {
        //
        // if one of our connections failed, and other connections are working,
        // then try to keep going
        //
        if (
                panic.getReason() == Panic.Reasons.NetworkThreadCrashed
                        && integerToHandle.size() > 1) {
            return false;
        }

        //
        // if we can't talk to anyone, ask the system what to do
        //
        else if (panic.getReason() == Panic.Reasons.NetworkThreadCrashed) {
            Panic newPanic = new Panic(panic.getCause(), Panic.Reasons.Network);
            Miranda.getInstance().panic(newPanic);
        }

        return false;
    }

    /**
     * Close the connection, without sending a sign-off.
     *
     * @param handleID The handle to disconnect from.
     */
    public void forceDisconnect(int handleID) {
        Handle handle = getHandle(handleID);

        if (null != handle) {
            handle.panic();
            clearHandle(handleID);
        }
    }

    /**
     * This is called when the {@link NetworkListener} gets a new connection.
     * <p>
     * <p>
     * The handle rturned can be used in susequent {@link SendMessageMessage} and
     * {@link CloseMessage}s.
     * </P>
     *
     * @param handle The new connection.
     * @return The handle for the new connection.
     */
    public int newConnection(Handle handle) {
        int handleId = nextHandle();

        setHandle(handleId, handle);

        return handleId;
    }

    public void sendOnNetwork(SendNetworkMessage sendNetworkMessage) throws NetworkException {
        Handle handle = integerToHandle.get(sendNetworkMessage.getHandle());

        if (handle == null) {
            throw new NetworkException("Unrecognized handle: " + sendNetworkMessage.getHandle(), NetworkException.Errors.UnrecognizedHandle);
        }

        handle.send(sendNetworkMessage);
    }

    /**
     * Tell the network to close a connection.
     */
    public void sendClose(BlockingQueue<Message> senderQueue, Object sender, int handle) {
        CloseMessage closeMessage = new CloseMessage(senderQueue, sender, handle);
        sendToMe(closeMessage);
    }

    /**
     * Ask the network to send a message.
     */
    public void sendMessage(BlockingQueue<Message> senderQueue, Object sender, int handle, WireMessage wireMessage) {
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
     * @param handle   The handle that it maps to
     */
    public void mapHandle(int handleID, Handle handle) {
        integerToHandle.put(handleID, handle);
    }

    public void sendNetworkMessage(BlockingQueue<Message> senderQueue, Object sender, int handle, WireMessage wireMessage) {
        SendNetworkMessage sendNetworkMessage = new SendNetworkMessage(senderQueue, sender, wireMessage, handle);
        sendToMe(sendNetworkMessage);
    }

    public void sendCloseMessage(BlockingQueue<Message> senderQueue, Object sender, int handle) {
        CloseMessage closeMessage = new CloseMessage(senderQueue, sender, handle);
        sendToMe(closeMessage);
    }
}

