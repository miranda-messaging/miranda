package com.ltsllc.miranda.operations;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry for long running Operatio
 */
public class OperationRegistry {
    private static OperationRegistry instance;

    public static OperationRegistry getInstance() {
        if (instance == null)
            initializeInstance();

        return instance;
    }

    public static synchronized void initializeInstance ()
    {
        if (instance == null) {
            instance = new OperationRegistry();
        }
    }

    private Map<String, Object> registry = new HashMap<>();

    public void register (String name, Object service) {
        registry.put(name, service);
    }

    public Object find (String name) {
        return registry.get(name);
    }

    public void unregister (String name) {
        registry.remove(name);
    }

}
