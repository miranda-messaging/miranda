package com.ltsllc.miranda.operations.deliveries;


import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;

public class DeliveryOperationReadyState extends State {
    private int attempts;

    public DeliveryOperation getDeliveryOperation () {
        return (DeliveryOperation) getContainer();
    }

    public DeliveryOperationReadyState (DeliveryOperation deliveryOperation) {
        super(deliveryOperation);
    }

    public State start () {
        Miranda.getInstance().getDeliveryManager().scheduleDelivery(getDeliveryOperation().getEvent(),
                getDeliveryOperation().getEventQueue().getSubscription().getDataUrl(),
                getDeliveryOperation().getQueue(), getDeliveryOperation());
        return getDeliveryOperation().getCurrentState();
    }
}
