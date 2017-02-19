package com.ltsllc.miranda.deliveries;

/**
 * Created by Clark on 1/8/2017.
 */
public class Deliveries {
    private static Deliveries ourInstance;

    public static synchronized void initialize (String directory)
    {
        if (null == ourInstance) {
            ourInstance = new Deliveries(directory);
        }
    }

    public static Deliveries getInstance () {
        return ourInstance;
    }

    private Deliveries (String directory)
    {

    }
}
