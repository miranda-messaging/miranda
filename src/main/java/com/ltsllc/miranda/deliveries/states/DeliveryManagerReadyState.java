package com.ltsllc.miranda.deliveries.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.deliveries.DeliveryManager;
import com.ltsllc.miranda.deliveries.messages.DeliverEventMessage;
import com.ltsllc.miranda.manager.states.DirectoryManagerReadyState;
import com.ltsllc.miranda.manager.states.ManagerReadyState;
import com.ltsllc.miranda.message.Message;
import com.sun.xml.internal.ws.server.sei.MessageFiller;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DeliveryManagerReadyState extends DirectoryManagerReadyState {
    private static Logger LOGGER = Logger.getLogger(DeliveryManagerReadyState.class);

    public HttpClient httpClient = HttpClients.createDefault();

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public DeliveryManagerReadyState(DeliveryManager deliveryManager) throws MirandaException {
        super(deliveryManager);
    }

    public DeliveryManager getDeliveryManager() {
        return (DeliveryManager) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getDeliveryManager().getCurrentState();

        switch (message.getSubject()) {
            case DeliverEvent: {
                DeliverEventMessage deliverEventMessage = (DeliverEventMessage) message;
                nextState = processDeliverEventMessage(deliverEventMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processDeliverEventMessage(DeliverEventMessage deliverEventMessage) {
        switch (deliverEventMessage.getEvent().getMethod()) {
            case POST: {
                deliverPost(deliverEventMessage.getEvent(), deliverEventMessage.getSubscription());
                break;
            }
        }
        return getDeliveryManager().getCurrentState();
    }

    public void deliverPost(Event event, Subscription subscription) {
        HttpPost httpPost = new HttpPost(subscription.getDataUrl());
        for (org.apache.http.Header header : event.getHeaders()) {
            // skip content-length because HttpClient will set it for us
            if (header.getName().equalsIgnoreCase("content-length"))
                continue;
            httpPost.addHeader(header.getName(), header.getValue());
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.getContent());
        InputStreamEntity inputStreamEntity = new InputStreamEntity(byteArrayInputStream);
        httpPost.setEntity(inputStreamEntity);

        try {
            getHttpClient().execute(httpPost);
        } catch (IOException e) {
            LOGGER.warn("Exception trying to deliver event", e);
            subscription.setOnline(false);
        }

    }
}
