package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.file.FileReadyState;
import com.ltsllc.miranda.file.MirandaFileReadyState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Clark on 2/19/2017.
 */
public class DeliveriesFileReadyState extends MirandaFileReadyState {
    public DeliveriesFileReadyState (DeliveriesFile deliveriesFile) {
        super(deliveriesFile);
    }

    public boolean equals (Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof DeliveriesFileReadyState))
            return false;

        Map<Object, Boolean> map = new HashMap<Object, Boolean>();
        return compare(map, o);
    }

    public boolean compare (Map<Object, Boolean> map, Object o) {
        if (map.containsKey(o)) {
            return map.get(o).booleanValue();
        }

        map.put(o, new Boolean(true));

        if (this == o) {
            return true;
        }

        if (null == o || !(o instanceof DeliveriesFileReadyState)) {
            map.put(o, Boolean.FALSE);
            return false;
        }

        DeliveriesFileReadyState other = (DeliveriesFileReadyState) o;
        return getContainer().compare(map, other.getContainer());
    }
}
