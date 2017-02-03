package com.ltsllc.miranda;



import java.util.concurrent.BlockingQueue;

/**
 * A stop state for a Miranda Subsystem.
 *
 * This class represents that the owner has reached a point where it
 * should be shut down.
 *
 * Created by Clark on 12/31/2016.
 */
public class StopState extends State {
    private static StopState ourInstance = new StopState();

    public static StopState getInstance () {
        return ourInstance;
    }

    public StopState()
    {
        super(null);
    }

    public State processMessage () {
        throw new IllegalStateException();
    }
}
