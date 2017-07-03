package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.clientinterface.results.EventResultObject;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.session.SessionServlet;

/**
 * A {@link SessionServlet} that deals with {@link com.ltsllc.miranda.test.TestNetworkListener.Event}s.
 *
 * <p>
 *     This class doesn't do much --- it just provides implementations for a few
 *     abstract methods({@link #createResultObject()} and {@link #getServletHolder()}),
 *     but most event-oriented classes inherit from it.
 * </p>
 */
abstract public class EventServlet extends SessionServlet {
    public EventHolder getEventHolder () {
        return EventHolder.getInstance();
    }

    @Override
    public ResultObject createResultObject() {
        return new EventResultObject();
    }

    @Override
    public ServletHolder getServletHolder() {
        return EventHolder.getInstance();
    }
}
