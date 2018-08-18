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

package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Mergeable;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.deliveries.messages.DeliverEventMessage;
import com.ltsllc.miranda.deliveries.messages.ScheduleDeliveryMessage;
import com.ltsllc.miranda.deliveries.states.DeliveryManagerReadyState;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.manager.states.DirectoryManagerStartState;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * A manager that manages a directory of event deliveries
 */
public class DeliveryManager extends DirectoryManager {
    public static final String NAME = "delivery manager";

    public DeliveryManager(String directory, int objectLimit, Reader reader, Writer writer) throws IOException, MirandaException {
        super(NAME, directory, objectLimit, reader, writer);

        setCurrentState(new DirectoryManagerStartState(this));
    }

    public void sendDeliverEvent(Event event, Subscription subscription, BlockingQueue<Message> senderQueue,
                            Object senderObjet) {
        DeliverEventMessage deliverEventMessage = new DeliverEventMessage(event, subscription, senderQueue, senderObjet);
        sendToMe(deliverEventMessage);
    }

    public State getReadyState () {
        try {
            return new DeliveryManagerReadyState(this);
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception while trying to get the DeliveryManager's ready state", e,
                    Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);

            return null;
        }
    }

    @Override
    public void processEntry(String string) {
        try {
            DeliveriesFile deliveriesFile = new DeliveriesFile(string, Miranda.getInstance().getReader(),
                    Miranda.getInstance().getWriter());
            deliveriesFile.start();
            getMap().put (string, deliveriesFile);

        } catch (Exception e) {
            Panic panic = new Panic("Exception while processing entry", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
        }
    }

    public void scheduleDelivery (Event event, String Url, BlockingQueue<Message> senderQueue, Object senderObject) {
        ScheduleDeliveryMessage scheduleDeliveryMessage = new ScheduleDeliveryMessage(event, Url, senderQueue, senderObject);
    }

}
