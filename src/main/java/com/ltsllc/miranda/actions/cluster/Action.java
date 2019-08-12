package com.ltsllc.miranda.actions.cluster;

import com.ltsllc.miranda.Consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An object that acts more or less independently of the rest of the system.
 */
public class Action extends Consumer {
    private static HashMap<Class, List<Action>> ourRegistered = new HashMap<Class, List<Action>>();

    public static synchronized void register (Action action) {
        List<Action> list = ourRegistered.get(action.getClass());

        if (list == null) {
            list = new ArrayList<Action>();
            list.add(action);
            ourRegistered.put (action.getClass(), list);
        } else if (!list.contains(action)) {
            list.add(action);
        }
    }

    public static synchronized  boolean find (Action action) {
        List<Action> list = ourRegistered.get(action);
        if (list == null)
            return false;
        return list.contains(action);
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action (String name) {
        setName(name);
    }
}
