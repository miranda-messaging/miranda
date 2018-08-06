package com.ltsllc.miranda.deliveries.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.deliveries.DeliveryThread;
import com.ltsllc.miranda.deliveries.messages.DeliverEventMessage;
import com.ltsllc.miranda.message.Message;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.Buffer;


public class DeliveryThreadReadyState extends State {
    private Logger LOGGER = Logger.getLogger(DeliveryThreadReadyState.class);
    private HttpClient httpClient;


    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public DeliveryThreadReadyState (DeliveryThread deliveryThread) {
        super(deliveryThread);

        HttpClient httpClient = HttpClients.createDefault();
        setHttpClient(httpClient);
    }

    public DeliveryThread getDeliveryThread () {
        return (DeliveryThread) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getDeliveryThread().getCurrentState();

        switch (message.getSubject()) {
            case DeliverEvent: {
                DeliverEventMessage deliverEventMessage = (DeliverEventMessage) message;
                nextState = processDeliveryMessage(deliverEventMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processDeliveryMessage (DeliverEventMessage deliverEventMessage) {
        getDeliveryThread().setEvent(deliverEventMessage.getEvent());
        getDeliveryThread().setSubscription(deliverEventMessage.getSubscription());
        if (deliverEventMessage.getSubscription().isOnline()) {
            deliverEvent(deliverEventMessage.getEvent(), deliverEventMessage.getSubscription());
        }

        return getDeliveryThread().getCurrentState();
    }

    public void deliverEvent (Event event, Subscription subscription) {
        switch (event.getMethod()) {
            case POST: {
                deliverPost(event, subscription);
                break;
            }

            case PUT: {
                deliverPut(event, subscription);
                break;
            }

            case DELETE: {
                deliverDelete(event, subscription);
                break;
            }
        }
    }

    public void deliverPost (Event event, Subscription subscription) {
        HttpPost httpPost = new HttpPost(subscription.getDataUrl());
        for (Header header : event.getHeaders()) {
            httpPost.addHeader(header);
        }

        try {
            getHttpClient().execute(httpPost);
        } catch (IOException e) {
            LOGGER.warn ("Exception trying to deliver event", e);
        }
    }

    public void deliverPut (Event event, Subscription subscription) {}
    public void deliverDelete (Event event, Subscription subscription) {}
}
