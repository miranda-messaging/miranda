package com.ltsllc.miranda.deliveries;

import com.google.gson.Gson;
import com.ltsllc.miranda.Subscription;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.file.Perishable;

import java.util.UUID;

/**
 * Created by Clark on 1/5/2017.
 */

/**
 * A successful delivery of an {@link Event} to a {@lin Subscription}.
 */
public class Delivery implements Perishable {
    private static Gson ourGson = new Gson();

    private String id;
    private String message;
    private long delivered;
    private String subscription;

    public Delivery (Event event, long delivered, Subscription subscription) {
        this.message = event.getId();
        this.id = UUID.randomUUID().toString();
        this.delivered = delivered;
        this.subscription = subscription.getName();
    }

    public String getMessage() {
        return message;
    }

    public long getDelivered() {
        return delivered;
    }

    public String getId() {

        return id;
    }

    /**
     *  Like the {@link Event} it is based on, an object of this class never expires.
     *
     * @param time The time to compare to.
     * @return This method always returns flse.
     */
    public boolean expired(long time) {
        return false;
    }

    public String toJson() {
        return ourGson.toJson(this);
    }
}
