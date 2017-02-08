package com.ltsllc.miranda;

/**
 * Created by Clark on 1/21/2017.
 */
public class StartState extends State {
    private static StartState ourInstance;

    public static StartState getInstance () {
        return ourInstance;
    }

    public static synchronized void initialize () {
        if (null == ourInstance) {
            ourInstance = new StartState();
        }
    }

    private StartState () {
        super(null);
    }
}
