package com.ltsllc.miranda.deliveries;

import java.util.Map;

/**
 * Created by Clark on 2/22/2017.
 */
public interface Comparer {
    public boolean compare (Map<Object,Boolean> map, Object o);
}
