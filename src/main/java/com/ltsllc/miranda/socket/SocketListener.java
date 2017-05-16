/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.socket;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * A class that sits around waiting for a network message, at which point
 * it sends a message and goes back to waiting.
 */
public class SocketListener implements Runnable {
    private static int BUFFER_SIZE = 4096;

    private static Gson ourGson = new Gson();
    private static Logger logger = Logger.getLogger(SocketListener.class);

    private Socket socket;
    private Thread thread;
    private BlockingQueue<Message> notify;
    private boolean keepGoing = true;
    private InputStream inputStream;

    public Socket getSocket() {
        return socket;
    }

    public Thread getThread() {
        return thread;
    }

    public BlockingQueue<Message> getNotify() {
        return notify;
    }

    public boolean keepGoing() {
        return keepGoing;
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public SocketListener(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public SocketListener(Socket socket, BlockingQueue<Message> notify) throws IOException {
        this.socket = socket;
        this.notify = notify;
        this.inputStream = socket.getInputStream();
        this.keepGoing = true;

        this.thread = new Thread(this);
    }

    public void start() {
        thread.start();
    }

    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (keepGoing()) {
            try {
                int bytesRead = inputStream.read(buffer);
                String s = new String(buffer, 0, bytesRead);
                String[] fields = s.split("\n");
                for (String json : fields) {
                    WireMessage pass1 = ourGson.fromJson(json, WireMessage.class);
                    Type type = getClass().forName(pass1.getClassName());
                    WireMessage wireMessage = ourGson.fromJson(json, type);

                    NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);
                    getNotify().put(networkMessage);
                }
            } catch (Exception e) {
                logger.error("Exception while listening for network message", e);
                Panic panic = new Panic(e, Panic.Reasons.NetworkThreadCrashed);
                boolean contintueWithPanic = Network.getInstance().panic(panic);

                logger.fatal("Network connection sutting down");

                if (contintueWithPanic) {
                    setKeepGoing(false);
                    forceClose();
                }
            }
        }
    }

    public void forceClose() {
        Utils.closeIgnoreExceptions(socket);
        setKeepGoing(false);
    }

    public void terminate () {
        setKeepGoing(false);
        getThread().interrupt();
    }
}
