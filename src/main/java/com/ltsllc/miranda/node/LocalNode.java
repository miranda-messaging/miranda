package com.ltsllc.miranda.node;

/**
 * represents the machine this running on
 */
public class LocalNode extends Node {
    public static LocalNode instance = new LocalNode();

    public static LocalNode getInstance() {
        return instance;
    }
}
