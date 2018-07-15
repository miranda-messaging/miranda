package com.ltsllc.miranda.deliveries.states;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.deliveries.DeliveryManager;
import com.ltsllc.miranda.manager.states.DirectoryManagerReadyState;
import com.ltsllc.miranda.manager.states.ManagerReadyState;

public class DeliveryManagerReadyState extends DirectoryManagerReadyState {
    public DeliveryManagerReadyState (DeliveryManager deliveryManager) throws MirandaException {
        super(deliveryManager);
    }
}
