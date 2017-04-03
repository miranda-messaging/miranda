package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.servlet.holder.ServletHolder;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 4/2/2017.
 */
public class TestServletHolder extends TestCase {
    public static class LocalRunnable implements Runnable {
        private Thread me;
        private Thread other;

        public Thread getOther() {
            return other;
        }

        public void setOther (Thread thread) {
            this.other = thread;
        }

        @Override
        public void run() {
            getOther().interrupt();
        }

        public LocalRunnable () {
            me = new Thread(this);
        }

        public void start () {
            me.start();
        }
    }

    public static class LocalWaiter implements Runnable {
        private Thread myThread;
        private ServletHolder servletHolder;
        private boolean notified;

        public boolean getNotified () {
            return notified;
        }

        public Thread getMyThread() {
            return myThread;
        }

        public ServletHolder getServletHolder() {
            return servletHolder;
        }

        public LocalWaiter (ServletHolder servletHolder) {
            myThread = new Thread(this);
            this.servletHolder = servletHolder;
        }

        public void start () {
            getMyThread().start();
        }

        public void run () {
            this.notified = getServletHolder().waitFor(1000);
        }
    }

    public static class Notifier implements Runnable {
        private Thread myThread;
        private ServletHolder servletHolder;

        public Thread getMyThread() {
            return myThread;
        }

        public ServletHolder getServletHolder() {
            return servletHolder;
        }

        public Notifier (ServletHolder servletHolder) {
            this.servletHolder = servletHolder;
            this.myThread = new Thread (this);
        }

        public void run () {
            getServletHolder().wake();
        }

        public void start () {
            getMyThread().start();
        }
    }

    private ServletHolder servletHolder;
    private LocalRunnable interrupter;

    public LocalRunnable getInterrupter() {
        return interrupter;
    }

    public ServletHolder getServletHolder() {
        return servletHolder;
    }

    public void reset () {
        super.reset();

        interrupter = null;
        servletHolder = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        interrupter = new LocalRunnable();
        servletHolder = new ServletHolder("test");
        servletHolder.start();
    }

    @Test
    public void testWaitForNotify () {
        LocalWaiter localWaiter = new LocalWaiter(getServletHolder());
        Notifier notifier = new Notifier(getServletHolder());
        localWaiter.start();
        notifier.start();

        pause(50);

        assert (localWaiter.getNotified());
    }

    @Test
    public void testWaitForInterrupted () {
        LocalWaiter localWaiter = new LocalWaiter(getServletHolder());
        getInterrupter().setOther(localWaiter.getMyThread());
        localWaiter.start();
        getInterrupter().start();

        pause(50);

        assert (!localWaiter.getNotified());
    }


}
