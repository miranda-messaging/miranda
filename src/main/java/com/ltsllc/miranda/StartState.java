package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/21/2017.
 */
public class StartState extends State {
    private static StartState ourInstane;

    public static StartState getInstance () {
        return ourInstane;
    }

    public static synchronized void initialize () {
        if (null == ourInstane) {
            ourInstane = new StartState();
        }
    }

    private StartState () {
        super(null);
    }
}
