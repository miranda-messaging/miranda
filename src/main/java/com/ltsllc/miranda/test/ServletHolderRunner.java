package com.ltsllc.miranda.test;

import com.ltsllc.miranda.servlet.ServletHolder;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 5/15/2017.
 */
abstract public class ServletHolderRunner implements Runnable {
    private static Logger logger = Logger.getLogger(ServletHolder.class);

    private Thread thread;
    private ServletHolder servletHolder;

    public ServletHolderRunner (ServletHolder servletHolder) {
        this.servletHolder = servletHolder;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public ServletHolder getServletHolder() {
        return servletHolder;
    }

    public void setServletHolder(ServletHolder servletHolder) {
        this.servletHolder = servletHolder;
    }

    public void start () {
        Thread newThread = new Thread(this);
        newThread.start();
    }
}
