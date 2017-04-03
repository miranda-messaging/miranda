package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.ServletHolder;

/**
 * Created by Clark on 4/2/2017.
 */
public class ServletHolderReadyState extends State {
    public ServletHolder getServletHolder () {
        return (ServletHolder) getContainer();
    }

    public ServletHolderReadyState (ServletHolder servletHolder) {
        super(servletHolder);
    }
}
